package com.lion.manage.expose.ward.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.ward.WardRoomDao;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.expose.ward.WardRoomExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午1:56
 */
@DubboService(interfaceClass = WardRoomExposeService.class)
public class WardRoomExposeServiceImpl extends BaseServiceImpl<WardRoom> implements WardRoomExposeService {

    @Autowired
    private WardRoomDao wardRoomDao;

    @Override
    public List<WardRoom> find(Long regionId) {
        return wardRoomDao.findByRegionId(regionId);
    }
}
