package com.lion.manage.expose.ward.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.ward.WardRoomSickbedDao;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.expose.ward.WardRoomSickbedExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午11:49
 */
@DubboService(interfaceClass = WardRoomSickbedExposeService.class)
public class WardRoomSickbedExposeServiceImpl extends BaseServiceImpl<WardRoomSickbed> implements WardRoomSickbedExposeService {

    @Autowired
    private WardRoomSickbedDao wardRoomSickbedDao;

    @Override
    public List<WardRoomSickbed> find(Long regionId) {
        return wardRoomSickbedDao.findByRegionId(regionId);
    }

    @Override
    public List<WardRoomSickbed> find(String bedCode) {
        return wardRoomSickbedDao.findByBedCodeLike("%"+bedCode+"%");
    }
}
