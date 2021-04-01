package com.lion.manage.service.ward.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.ward.WardDao;
import com.lion.manage.dao.ward.WardRoomDao;
import com.lion.manage.entity.ward.Ward;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.service.ward.WardRoomService;
import com.lion.manage.service.ward.WardRoomSickbedService;
import com.lion.manage.service.ward.WardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
