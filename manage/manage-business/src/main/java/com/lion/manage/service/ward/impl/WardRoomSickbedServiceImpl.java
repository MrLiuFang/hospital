package com.lion.manage.service.ward.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.ward.WardRoomSickbedDao;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.service.ward.WardRoomSickbedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:17
 */
@Service
public class WardRoomSickbedServiceImpl extends BaseServiceImpl<WardRoomSickbed> implements WardRoomSickbedService {

    @Autowired
    private WardRoomSickbedDao wardRoomSickbedDao;
}
