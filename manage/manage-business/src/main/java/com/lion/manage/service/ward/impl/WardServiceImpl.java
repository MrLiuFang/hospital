package com.lion.manage.service.ward.impl;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.ward.WardDao;
import com.lion.manage.dao.ward.WardRoomDao;
import com.lion.manage.dao.ward.WardRoomSickbedDao;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.ward.Ward;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.entity.ward.dto.AddWardDto;
import com.lion.manage.entity.ward.dto.AddWardRoomDto;
import com.lion.manage.entity.ward.dto.UpdateWardDto;
import com.lion.manage.entity.ward.dto.UpdateWardRoomDto;
import com.lion.manage.service.ward.WardRoomService;
import com.lion.manage.service.ward.WardRoomSickbedService;
import com.lion.manage.service.ward.WardService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    @Override
    public int deleteByDepartmentId(Long departmentId) {
        List<Ward> list = wardDao.findByDepartmentId(departmentId);
        list.forEach(ward -> {
            wardRoomService.deleteByWardId(ward.getId());
        });
        return wardDao.deleteByDepartmentId(departmentId);
    }

    @Override
    public void add(AddWardDto addWardDto) {
        Ward ward = new Ward();
        BeanUtils.copyProperties(addWardDto,ward);
        assertRepeat(addWardDto,null);
        ward = save(ward);
        wardRoomService.save(addWardDto.getWardRoom(),ward.getId());
    }

    @Override
    public void update(UpdateWardDto updateWardDto) {
        Ward ward = new Ward();
        BeanUtils.copyProperties(updateWardDto,ward);
        assertRepeat(updateWardDto,ward.getId());
        ward = save(ward);
        wardRoomService.save(updateWardDto.getWardRoom(),ward.getId());
    }

    @Override
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            List<WardRoom> list = wardRoomDao.findByWardId(deleteDto.getId());
            list.forEach(wardRoom -> {
                wardRoomSickbedDao.deleteByWardRoomId(wardRoom.getId());
            });
            wardRoomDao.deleteByWardId(deleteDto.getId());
            deleteById(deleteDto.getId());
        });
    }

    private void assertRepeat(Ward wardDto,Long id){
        assertNameExist(wardDto.getName(),id);
        if (wardDto instanceof AddWardDto){
            assertRepeat(((AddWardDto)wardDto).getWardRoom());
        }else if (wardDto instanceof UpdateWardDto){
            assertRepeat(((UpdateWardDto)wardDto).getWardRoom());
        }
    }


    private void assertRepeat(List<? extends WardRoom> list){
        Map<String,String> wardRoomCodeHash = new ConcurrentHashMap<String,String>();
        list.forEach(wardRoom->{
            if (wardRoomCodeHash.containsKey(wardRoom.getCode())){
                BusinessException.throwException("该病房存在重复的房间编号("+wardRoom.getCode()+")");
            }
            wardRoomCodeHash.put(wardRoom.getCode(),"");
            if (wardRoom instanceof AddWardRoomDto){
                assertRepeat(((AddWardRoomDto)wardRoom).getWardRoomSickbed(),wardRoom.getCode());
            }else if (wardRoom instanceof UpdateWardRoomDto){
                assertRepeat(((UpdateWardRoomDto)wardRoom).getWardRoomSickbed(),wardRoom.getCode());
            }
        });
    }

    private void assertRepeat(List<? extends WardRoomSickbed> list,String code){
        Map<String,String> wardRoomSickbedCodeHash = new ConcurrentHashMap<String,String>();
        list.forEach(wardRoomSickbed->{
            if (wardRoomSickbedCodeHash.containsKey(wardRoomSickbed.getBedCode())){
                BusinessException.throwException(code+"-房间存在重复的床位("+wardRoomSickbed.getBedCode()+")");
            }
            wardRoomSickbedCodeHash.put(wardRoomSickbed.getBedCode(),"");
        });
    }

    private void assertNameExist(String name, Long id) {
        Ward ward = wardDao.findFirstByName(name);
        if (Objects.isNull(id) && Objects.nonNull(ward) ){
            BusinessException.throwException("该病房名称已存在");
        }
        if (Objects.nonNull(id) && Objects.nonNull(ward) && !ward.getId().equals(id)){
            BusinessException.throwException("该病房名称已存在");
        }
    }

}
