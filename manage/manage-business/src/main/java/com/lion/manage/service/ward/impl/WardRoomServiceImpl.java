package com.lion.manage.service.ward.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.ward.WardRoomDao;
import com.lion.manage.dao.ward.WardRoomSickbedDao;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.entity.ward.dto.AddWardRoomDto;
import com.lion.manage.entity.ward.dto.UpdateWardRoomDto;
import com.lion.manage.service.ward.WardRoomService;
import com.lion.manage.service.ward.WardRoomSickbedService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    @Override
    public int deleteByWardId(Long wardId) {
        List<WardRoom> list = wardRoomDao.findByWardId(wardId);
        list.forEach(wardRoom -> {
            wardRoomSickbedDao.deleteByWardRoomId(wardRoom.getId());
        });
        return wardRoomDao.deleteByWardId(wardId);
    }

    @Override
    public void save(List<? extends WardRoom> wardRoomDto, Long wardId) {
        if (Objects.isNull(wardId)){
            return;
        }
        deleteByWardId(wardId);
        wardRoomDto.forEach(dto->{
            WardRoom wardRoom = new WardRoom();
            BeanUtils.copyProperties(dto,wardRoom);
            wardRoom.setWardId(wardId);
            wardRoom = save(wardRoom);
            if (dto instanceof AddWardRoomDto){
                wardRoomSickbedService.save(((AddWardRoomDto)dto).getWardRoomSickbed(),wardRoom.getId());
            }else if ((dto instanceof UpdateWardRoomDto)){
                wardRoomSickbedService.save(((UpdateWardRoomDto)dto).getWardRoomSickbed(),wardRoom.getId());
            }

        });
    }

    @Override
    public List<WardRoom> find(Long wardId) {
        return wardRoomDao.findByWardId(wardId);
    }
}
