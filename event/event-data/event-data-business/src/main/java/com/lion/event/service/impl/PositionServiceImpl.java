package com.lion.event.service.impl;

import com.lion.common.enums.Type;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.dao.PositionDao;
import com.lion.event.entity.Position;
import com.lion.event.entity.vo.ListPositionVo;
import com.lion.event.service.CurrentPositionService;
import com.lion.event.service.PositionService;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

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

    @DubboReference
    private TagExposeService tagExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

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
    public IPageResultData<List<Position>> list(Long pi, Long adi, Long ri, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(pi)) {
            criteria.and("pi").is(pi);
        }
        if (Objects.nonNull(adi)) {
            criteria.and("adi").is(adi);
        }
        if (Objects.nonNull(ri)) {
            criteria.and("ri").is(ri);
        }
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(30);
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
    public List<String> personAllRegion(Long personId, Long regionId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return positionDao.personAllRegion(personId,regionId , startDateTime, endDateTime);
    }

    @Override
    public IPageResultData<List<ListPositionVo>> tagPosition(TagPurpose tagPurpose, Long regionId, Long departmentId, String deviceName, String tagCode, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Map<String, Object> searchParameter = new HashMap<>();
        if (Objects.nonNull(tagPurpose)){
            searchParameter.put(SearchConstant.EQUAL+"_tagPurpose",tagPurpose);
        }
        if (StringUtils.hasText(deviceName)){
            searchParameter.put(SearchConstant.LIKE+"_deviceName","%"+deviceName+"%");
        }
        if (StringUtils.hasText(tagCode)){
            searchParameter.put(SearchConstant.LIKE+"_tagCode","%"+tagCode+"%");
        }
        List<Tag> tagList = tagExposeService.find(searchParameter);
        List<Long> tagIds = new ArrayList<>();
        tagList.forEach(tag -> {
            tagIds.add(tag.getId());
        });
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (tagIds.size()>0) {
            criteria.and("ti").in(tagIds);
        }
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(30);
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
        List<ListPositionVo> returnList = new ArrayList<>();
        items.forEach(position -> {
            ListPositionVo vo = new ListPositionVo();
            Tag tag = tagExposeService.findById(position.getTi());
            BeanUtils.copyProperties(position,vo);
            if (Objects.nonNull(tag)){
                vo.setDeviceName(tag.getDeviceName());
                vo.setTagCode(tag.getTagCode());
                vo.setTagPurpose(tag.getPurpose());
                Department department = departmentExposeService.findById(tag.getDepartmentId());
                if (Objects.nonNull(department)) {
                    vo.setDepartmentName(department.getName());
                }
            }
            returnList.add(vo);
        });
        return new PageResultData(returnList,lionPage,0L);
    }
}
