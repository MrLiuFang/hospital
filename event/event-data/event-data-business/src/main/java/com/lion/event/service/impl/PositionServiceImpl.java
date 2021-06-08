package com.lion.event.service.impl;

import com.lion.common.enums.Type;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.event.dao.PositionDao;
import com.lion.event.entity.Position;
import com.lion.event.service.CurrentPositionService;
import com.lion.event.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    private MongoTemplate mongoTemplate;

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

    @Override
    public IPageResultData<List<Position>> list(Long pi, Long ai, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(pi)) {
            criteria.and("pi").is(pi);
        }
        if (Objects.nonNull(ai)) {
            criteria.and("ai").is(ai);
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            criteria.andOperator( Criteria.where("ddt").gte(startDateTime) ,Criteria.where("ddt").lte(endDateTime));
        }else if (Objects.nonNull(startDateTime) &&  Objects.isNull(endDateTime)) {
            criteria.and("ddt").gte(startDateTime);
        }else if (Objects.isNull(startDateTime) &&  Objects.nonNull(endDateTime)) {
            criteria.and("ddt").lte(endDateTime);
        }
        query.addCriteria(criteria);
        query.with(lionPage);
        query.with(Sort.by(Sort.Direction.DESC,"ddt"));
        List<Position> items = mongoTemplate.find(query,Position.class);
//        long count = mongoTemplate.count(query, DeviceData.class);
//        PageableExecutionUtils.getPage(items, lionPage, () -> count);
        IPageResultData<List<Position>> pageResultData =new PageResultData<>(items,lionPage,0L);
        return pageResultData;
    }

    @Override
    public List<String> personAllRegion(Long personId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return positionDao.personAllRegion(personId, startDateTime, endDateTime);
    }
}
