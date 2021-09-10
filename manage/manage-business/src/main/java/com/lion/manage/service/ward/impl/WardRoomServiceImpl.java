package com.lion.manage.service.ward.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.core.LionPage;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.ward.WardRoomDao;
import com.lion.manage.dao.ward.WardRoomSickbedDao;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.entity.ward.dto.AddWardRoomDto;
import com.lion.manage.entity.ward.dto.UpdateWardRoomDto;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.ward.WardRoomService;
import com.lion.manage.service.ward.WardRoomSickbedService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:15
 */
@Service
public class WardRoomServiceImpl extends BaseServiceImpl<WardRoom> implements WardRoomService {

    @Autowired
    private WardRoomDao wardRoomDao;

    @Autowired
    private WardRoomSickbedDao wardRoomSickbedDao;

    @Autowired
    private WardRoomSickbedService wardRoomSickbedService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public int deleteByWardId(Long wardId) {
        List<WardRoom> list = wardRoomDao.findByWardId(wardId);
        list.forEach(wardRoom -> {
            wardRoomSickbedDao.deleteByWardRoomId(wardRoom.getId());
        });
        return wardRoomDao.deleteByWardId(wardId);

    }

    @Override
    @Transactional
    public void save(List<? extends WardRoom> wardRoomDto, Long wardId) {
        if (Objects.isNull(wardId)){
            return;
        }
//        deleteByWardId(wardId);
        List<WardRoom> list = wardRoomDao.findByWardId(wardId);
        if (Objects.nonNull(wardRoomDto)) {
            wardRoomDto.forEach(dto -> {
                WardRoom wardRoom = new WardRoom();
                Boolean isExist = false;
                for (WardRoom wr: list){
                    if (Objects.equals(wr.getCode(),dto.getCode())) {
                        isExist = true;
                        wardRoom = wr;
                    }
                }
                if (!isExist) {
                    BeanUtils.copyProperties(dto, wardRoom);
                    wardRoom.setWardId(wardId);
                    wardRoom = save(wardRoom);
                }
                if (dto instanceof AddWardRoomDto) {
                    wardRoomSickbedService.save(((AddWardRoomDto) dto).getWardRoomSickbed(), wardRoom.getId());
                } else if ((dto instanceof UpdateWardRoomDto)) {
                    wardRoomSickbedService.save(((UpdateWardRoomDto) dto).getWardRoomSickbed(), wardRoom.getId());
                }
                redisTemplate.opsForValue().set(RedisConstants.WARD_ROOM+wardRoom.getId(),wardRoom, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            });
        }

        if (Objects.nonNull(list) && list.size()>0) {
            list.forEach(wardRoom -> {
                Boolean isDelete = true;
                if (Objects.nonNull(wardRoomDto) && wardRoomDto.size()>0) {
                    for (WardRoom wr : wardRoomDto) {
                        if (Objects.equals(wardRoom.getCode(), wr.getCode())) {
                            isDelete = false;
                        }
                    }
                    if (isDelete) {
                        deleteById(wardRoom.getId());
                        redisTemplate.delete(RedisConstants.WARD_ROOM+wardRoom.getId());
                        List<WardRoomSickbed> wardRoomSickbeds = wardRoomSickbedDao.findByWardRoomId(wardRoom.getId());
                        wardRoomSickbeds.forEach(wardRoomSickbed -> {
                            redisTemplate.delete(RedisConstants.WARD_ROOM_SICKBED+wardRoomSickbed.getId());
                        });
                        wardRoomSickbedDao.deleteByWardRoomId(wardRoom.getId());
                    }
                }
            });
        }
    }

    @Override
    public List<WardRoom> find(Long wardId) {
        return wardRoomDao.findByWardId(wardId);
    }

    @Override
    public Page<WardRoom> list(Long departmentId, Long wardId, LionPage lionPage) {
        return wardRoomDao.list(departmentId, wardId, lionPage);
    }
}
