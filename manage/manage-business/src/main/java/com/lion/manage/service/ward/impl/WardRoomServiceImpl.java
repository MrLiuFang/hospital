package com.lion.manage.service.ward.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.ward.WardRoomDao;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.service.ward.WardRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:15
 */
@Service
public class WardRoomServiceImpl extends BaseServiceImpl<WardRoom> implements WardRoomService {

    @Autowired
    private WardRoomDao wardRoomDao;
}
