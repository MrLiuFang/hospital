package com.lion.event.service.impl;

import com.lion.event.dao.PositionDao;
import com.lion.event.entity.Position;
import com.lion.event.service.CurrentPositionService;
import com.lion.event.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:12
 **/
@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    private PositionDao positionDao;

    @Autowired
    private CurrentPositionService currentPositionService;

    @Override
    public void save(Position position) {
        positionDao.save(position);
        currentPositionService.save(position);
    }
}
