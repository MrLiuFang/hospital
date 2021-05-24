package com.lion.event.service.impl;

import com.lion.common.enums.Type;
import com.lion.event.dao.PositionDao;
import com.lion.event.entity.Position;
import com.lion.event.service.CurrentPositionService;
import com.lion.event.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public List<Position> findUserId(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return positionDao.find(userId, Type.STAFF , startDateTime, endDateTime);
    }

    @Override
    public List<Position> findByAssetsId(Long assetsId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return positionDao.find(assetsId, Type.ASSET , startDateTime, endDateTime);
    }
}
