package com.lion.event.service.impl;

import com.lion.event.dao.CurrentPositionDao;
import com.lion.event.entity.CurrentPosition;
import com.lion.event.entity.Position;
import com.lion.event.entity.vo.RegionStatisticsDetails;
import com.lion.event.service.CurrentPositionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/15 下午3:03
 **/
@Service
public class CurrentPositionServiceImpl implements CurrentPositionService {

    @Autowired
    private CurrentPositionDao currentPositionDao;

    @Override
    public void save(Position position) {
        CurrentPosition currentPosition = new CurrentPosition();
        BeanUtils.copyProperties(position,currentPosition);
        CurrentPosition exampleCurrentPosition = new CurrentPosition();
        if (Objects.nonNull(position.getPi())) {
            exampleCurrentPosition.setPi(position.getPi());
        }
        if (Objects.nonNull(position.getAdi())) {
            exampleCurrentPosition.setAdi(position.getAdi());
        }
        if (Objects.nonNull(position.getTi())) {
            exampleCurrentPosition.setTi(position.getTi());
        }
        Example<CurrentPosition> example = Example.of(exampleCurrentPosition);
        Optional<CurrentPosition> optional = currentPositionDao.findOne(example);
        if (optional.isPresent()){
            CurrentPosition oldCurrentPosition = optional.get();
            currentPosition.set_id(oldCurrentPosition.get_id());
        }
        currentPositionDao.save(currentPosition);
    }

    @Override
    public CurrentPosition find(Long userId) {
        CurrentPosition exampleCurrentPosition = new CurrentPosition();
        if (Objects.nonNull(userId)) {
            exampleCurrentPosition.setPi(userId);
        }
        Example<CurrentPosition> example = Example.of(exampleCurrentPosition);
        Optional<CurrentPosition> optional = currentPositionDao.findOne(example);
        if (optional.isPresent()) {
            CurrentPosition currentPosition = optional.get();
            return currentPosition;
        }
        return null;
    }

    @Override
    public CurrentPosition findByTagId(Long tagId) {
        CurrentPosition exampleCurrentPosition = new CurrentPosition();
        if (Objects.nonNull(tagId)) {
            exampleCurrentPosition.setTi(tagId);
        }
        Example<CurrentPosition> example = Example.of(exampleCurrentPosition);
        Optional<CurrentPosition> optional = currentPositionDao.findOne(example);
        if (optional.isPresent()) {
            CurrentPosition currentPosition = optional.get();
            return currentPosition;
        }
        return null;
    }

    @Override
    public Map<Long, RegionStatisticsDetails> groupCount(Long buildFloorId, Map<Long, RegionStatisticsDetails> map) {
        return currentPositionDao.groupCount(buildFloorId, map);
    }
}
