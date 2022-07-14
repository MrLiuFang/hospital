package com.lion.event.service.impl;

import com.lion.event.dao.CurrentPositionDao;
import com.lion.event.entity.CurrentPosition;
import com.lion.event.entity.Position;
import com.lion.event.entity.vo.RegionStatisticsDetails;
import com.lion.event.service.CurrentPositionService;
import org.apache.tomcat.util.http.fileupload.impl.IOFileUploadException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.lion.core.Optional;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/15 下午3:03
 **/
@Service
public class CurrentPositionServiceImpl implements CurrentPositionService {

    @Autowired
    private CurrentPositionDao currentPositionDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Position position) {
        CurrentPosition currentPosition = new CurrentPosition();
        BeanUtils.copyProperties(position,currentPosition);

        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(position.getPi())) {
            criteria.and("pi").is(position.getPi());
        }
        if (Objects.nonNull(position.getAdi())) {
            criteria.and("adi").is(position.getAdi());
        }
        if (Objects.nonNull(position.getTi())) {
            criteria.and("ti").is(position.getTi());
        }
        query.addCriteria(criteria);
        List<CurrentPosition> list = mongoTemplate.find(query,CurrentPosition.class);
        CurrentPosition oldCurrentPosition = null;
        if (list.size()>1) {
            list.forEach(currentPosition1 -> {
                mongoTemplate.remove(currentPosition1);
            });
        }
        if (list.size()==1) {
            oldCurrentPosition = list.get(0);
            if (Objects.nonNull(oldCurrentPosition)){
                currentPosition.set_id(oldCurrentPosition.get_id());
            }
        }
        currentPositionDao.save(currentPosition);
    }

    @Override
    public CurrentPosition find(Long pi) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(pi)) {
            criteria.and("pi").is(pi);
        }
        query.addCriteria(criteria);
        CurrentPosition currentPosition = mongoTemplate.findOne(query,CurrentPosition.class);
        return currentPosition;
    }

    @Override
    public CurrentPosition findByTagId(Long tagId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(tagId)) {
            criteria.and("ti").is(tagId);
        }
        query.addCriteria(criteria);
        CurrentPosition currentPosition = mongoTemplate.findOne(query,CurrentPosition.class);
        return currentPosition;
    }

    @Override
    public CurrentPosition findByAssetsId(Long assetsId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(assetsId)) {
            criteria.and("adi").is(assetsId);
        }
        query.addCriteria(criteria);
        CurrentPosition currentPosition = mongoTemplate.findOne(query,CurrentPosition.class);
        return currentPosition;
    }

    @Override
    public Map<Long, RegionStatisticsDetails> groupCount(Long buildFloorId, Map<Long, RegionStatisticsDetails> map) {
        return currentPositionDao.groupCount(buildFloorId, map);
    }
}
