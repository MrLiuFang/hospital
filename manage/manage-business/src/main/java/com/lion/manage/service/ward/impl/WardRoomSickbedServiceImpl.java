package com.lion.manage.service.ward.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.ward.WardRoomSickbedDao;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.entity.ward.vo.ListWardRoomSickbedVo;
import com.lion.manage.service.ward.WardRoomSickbedService;
import com.lion.person.expose.person.PatientExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:17
 */
@Service
public class WardRoomSickbedServiceImpl extends BaseServiceImpl<WardRoomSickbed> implements WardRoomSickbedService {

    @Autowired
    private WardRoomSickbedDao wardRoomSickbedDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private PatientExposeService patientExposeService;

    @Override
    public void save(List<? extends WardRoomSickbed> addWardRoomSickbedDto, Long wardRoomId) {
        if (Objects.isNull(addWardRoomSickbedDto)){
            return;
        }
        List<WardRoomSickbed> list = wardRoomSickbedDao.findByWardRoomId(wardRoomId);
        addWardRoomSickbedDto.forEach(dto->{
            Boolean isExist = false;
            for (WardRoomSickbed wrs : list) {
                if (Objects.equals(dto.getBedCode(),wrs.getBedCode())) {
                    isExist = true;
                }
            }
            if (!isExist) {
                WardRoomSickbed wardRoomSickbed = new WardRoomSickbed();
                BeanUtils.copyProperties(dto,wardRoomSickbed);
                wardRoomSickbed.setWardRoomId(wardRoomId);
                save(wardRoomSickbed);
                redisTemplate.opsForValue().set(RedisConstants.WARD_ROOM_SICKBED+wardRoomSickbed.getId(),wardRoomSickbed,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        });

        list.forEach(wrs -> {
            Boolean isDelete = true;
            for (WardRoomSickbed wardRoomSickbed : addWardRoomSickbedDto) {
                if (Objects.equals(wrs.getBedCode(), wardRoomSickbed.getBedCode())) {
                    isDelete = false;
                }
            }
            if (isDelete) {
                deleteById(wrs.getId());
                redisTemplate.delete(RedisConstants.WARD_ROOM_SICKBED+wrs.getId());
            }
        });
    }

    @Override
    public List<WardRoomSickbed> find(Long wardRoomId) {
        return wardRoomSickbedDao.findByWardRoomId(wardRoomId);
    }

    @Override
    public List<WardRoomSickbed> findByRegionId(Long regionId) {
        return wardRoomSickbedDao.findByRegionId(regionId);
    }

    @Override
    public Page<ListWardRoomSickbedVo> list(String bedCode, Long departmentId, Long wardId, Long wardRoomId, LionPage lionPage) {
        Page<WardRoomSickbed> page = wardRoomSickbedDao.list(bedCode, departmentId, wardId, wardRoomId, lionPage);
        List<WardRoomSickbed> list = page.getContent();
        List<ListWardRoomSickbedVo> returnList = new ArrayList<>();
        list.forEach(wardRoomSickbed -> {
            ListWardRoomSickbedVo vo = new ListWardRoomSickbedVo();
            BeanUtils.copyProperties(wardRoomSickbed,vo);
            List<Long> sickbedIds = new ArrayList<>();
            sickbedIds.add(wardRoomSickbed.getId());
            vo.setIsUse(patientExposeService.countUseSickbed(sickbedIds)>0);
            returnList.add(vo);
        });
        return new PageResultData(returnList,lionPage,page.getTotalElements());
    }

    @Override
    public void updateRegionId(List<Long> ids, Long regionId) {
        wardRoomSickbedDao.updateRegionIdIsNull(regionId);
        if (Objects.nonNull(ids) && ids.size()>0) {
            wardRoomSickbedDao.updateRegionId(regionId, ids);
        }
    }
}
