package com.lion.event.expose.service.impl;

import com.lion.event.dao.CurrentPositionDao;
import com.lion.event.expose.service.CurrentPositionExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2021-08-17 10:20
 **/
@DubboService(interfaceClass = CurrentPositionExposeService.class)
public class CurrentPositionExposeServiceImpl implements CurrentPositionExposeService {

    @Autowired
    private CurrentPositionDao currentPositionDao;

    @Override
    public void delete(Long pi, Long adi, Long ti) {
        if (Objects.nonNull(pi)) {
            currentPositionDao.deleteByPi(pi);
        }
        if (Objects.nonNull(adi)) {
            currentPositionDao.deleteByAdi(adi);
        }
        if (Objects.nonNull(ti)) {
            currentPositionDao.deleteByTi(ti);
        }
    }
}
