package com.lion.manage.service.ward.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.ward.WardRoomSickbedDao;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.service.ward.WardRoomSickbedService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:17
 */
@Service
public class WardRoomSickbedServiceImpl extends BaseServiceImpl<WardRoomSickbed> implements WardRoomSickbedService {

    @Autowired
    private WardRoomSickbedDao wardRoomSickbedDao;

    @Override
    public void save(List<? extends WardRoomSickbed> addWardRoomSickbedDto, Long wardRoomId) {
        if (Objects.isNull(addWardRoomSickbedDto)){
            return;
        }
        addWardRoomSickbedDto.forEach(dto->{
            WardRoomSickbed wardRoomSickbed = new WardRoomSickbed();
            BeanUtils.copyProperties(dto,wardRoomSickbed);
            wardRoomSickbed.setWardRoomId(wardRoomId);
            save(wardRoomSickbed);
        });
    }

    @Override
    public List<WardRoomSickbed> find(Long wardRoomId) {
        return wardRoomSickbedDao.findByWardRoomId(wardRoomId);
    }
}
