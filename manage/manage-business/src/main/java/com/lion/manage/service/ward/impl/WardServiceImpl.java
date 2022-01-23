package com.lion.manage.service.ward.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.ward.WardDao;
import com.lion.manage.dao.ward.WardRoomDao;
import com.lion.manage.dao.ward.WardRoomSickbedDao;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.ward.Ward;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.entity.ward.dto.AddWardDto;
import com.lion.manage.entity.ward.dto.AddWardRoomDto;
import com.lion.manage.entity.ward.dto.UpdateWardDto;
import com.lion.manage.entity.ward.dto.UpdateWardRoomDto;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.ward.WardRoomService;
import com.lion.manage.service.ward.WardRoomSickbedService;
import com.lion.manage.service.ward.WardService;
import com.lion.utils.MessageI18nUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:12
 */
@Service
public class WardServiceImpl extends BaseServiceImpl<Ward> implements WardService {

    @Autowired
    private WardDao wardDao;

    @Autowired
    private WardRoomDao wardRoomDao;

    @Autowired
    private WardRoomSickbedDao wardRoomSickbedDao;

    @Autowired
    private WardRoomService wardRoomService;

    @Autowired
    private WardRoomSickbedService wardRoomSickbedService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void deleteByDepartmentId(Long departmentId) {
        List<Ward> list = wardDao.findByDepartmentId(departmentId);
        List<DeleteDto> deleteDtoList = new ArrayList<DeleteDto>();
        list.forEach(ward -> {
            DeleteDto  deleteDto = new DeleteDto();
            deleteDto.setId(ward.getId());
            deleteDtoList.add(deleteDto);
        });
        delete(deleteDtoList);
    }

    @Override
    @Transactional
    public void add(AddWardDto addWardDto) {
        Ward ward = new Ward();
        BeanUtils.copyProperties(addWardDto,ward);
        assertDepartmentExist(addWardDto.getDepartmentId());
        assertRepeat(addWardDto,null);
        ward = save(ward);
        wardRoomService.save(addWardDto.getWardRoom(),ward.getId());
    }

    @Override
    @Transactional
    public void update(UpdateWardDto updateWardDto) {
        Ward ward = new Ward();
        BeanUtils.copyProperties(updateWardDto,ward);
        assertDepartmentExist(updateWardDto.getDepartmentId());
        assertRepeat(updateWardDto,ward.getId());
        ward = save(ward);
        wardRoomService.save(updateWardDto.getWardRoom(),ward.getId());
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            List<WardRoom> list = wardRoomDao.findByWardId(deleteDto.getId());
            list.forEach(wardRoom -> {
                wardRoomSickbedDao.deleteByWardRoomId(wardRoom.getId());
                List<WardRoomSickbed> wardRoomSickbeds = wardRoomSickbedDao.findByWardRoomId(wardRoom.getId());
                wardRoomSickbeds.forEach(wardRoomSickbed -> {
                    redisTemplate.delete(RedisConstants.WARD_ROOM_SICKBED+wardRoomSickbed.getId());
                });
                redisTemplate.delete(RedisConstants.WARD_ROOM+wardRoom.getId());
            });
            wardRoomDao.deleteByWardId(deleteDto.getId());
            deleteById(deleteDto.getId());
        });
    }

    private void assertRepeat(Ward wardDto,Long id){
        assertNameExist(wardDto.getName(),id);
        if (wardDto instanceof AddWardDto){
            assertRepeat(((AddWardDto)wardDto).getWardRoom());
//            assertRegionExist(((AddWardDto)wardDto).getWardRoom());
        }else if (wardDto instanceof UpdateWardDto){
            assertRepeat(((UpdateWardDto)wardDto).getWardRoom());
//            assertRegionExist(((UpdateWardDto)wardDto).getWardRoom());
        }

    }


    private void assertRepeat(List<? extends WardRoom> list){
        Map<String,String> wardRoomCodeHash = new ConcurrentHashMap<String,String>();
        if (Objects.nonNull(list)) {
            list.forEach(wardRoom -> {
                if (!StringUtils.hasText(wardRoom.getCode())) {
                    BusinessException.throwException(MessageI18nUtil.getMessage("2000095"));
                }
                if (wardRoomCodeHash.containsKey(wardRoom.getCode())) {
                    BusinessException.throwException(MessageI18nUtil.getMessage("2000096",new String[]{wardRoom.getCode()}));
                }
                wardRoomCodeHash.put(wardRoom.getCode(), "");
                if (wardRoom instanceof AddWardRoomDto) {
                    assertRepeat(((AddWardRoomDto) wardRoom).getWardRoomSickbed(), wardRoom.getCode());
                } else if (wardRoom instanceof UpdateWardRoomDto) {
                    assertRepeat(((UpdateWardRoomDto) wardRoom).getWardRoomSickbed(), wardRoom.getCode());
                }
            });
        }
    }

    private void assertRepeat(List<? extends WardRoomSickbed> list,String code){
        Map<String,String> wardRoomSickbedCodeHash = new ConcurrentHashMap<String,String>();
        if (Objects.nonNull(list)) {
            list.forEach(wardRoomSickbed -> {
                if (!StringUtils.hasText(wardRoomSickbed.getBedCode())) {
                    BusinessException.throwException(MessageI18nUtil.getMessage("2000057"));
                }
                if (wardRoomSickbedCodeHash.containsKey(wardRoomSickbed.getBedCode())) {
                    BusinessException.throwException(code +MessageI18nUtil.getMessage("2000097",new String[]{wardRoomSickbed.getBedCode()}));
                }
                wardRoomSickbedCodeHash.put(wardRoomSickbed.getBedCode(), "");
            });
        }
    }
    private void assertDepartmentExist(Long id) {
        com.lion.core.Optional<Department> optional = this.departmentService.findById(id);
        if (optional.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000069"));
        }
    }
//    private void assertRegionExist(List<? extends WardRoom> wardRoomDto) {
//        wardRoomDto.forEach(wardRoom -> {
//            if (Objects.isNull(wardRoom.getRegionId())){
//                BusinessException.throwException(wardRoom.getCode()+"房间编码请选择区域");
//            }
//            if ( Objects.isNull(regionService.findById(wardRoom.getRegionId()))){
//                BusinessException.throwException(wardRoom.getCode()+"房间编码选择区域的区域不存在");
//            }
//        });
//    }
    private void assertNameExist(String name, Long id) {
        Ward ward = wardDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(ward)) || (Objects.nonNull(id) && Objects.nonNull(ward) && !Objects.equals(ward.getId(),id) ) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000098"));

        }
    }

}
