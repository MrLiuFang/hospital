package com.lion.event.service.impl;

import com.lion.common.enums.Type;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.dao.HumitureRecordDao;
import com.lion.event.entity.HumitureRecord;
import com.lion.event.entity.vo.ListHumitureRecordVo;
import com.lion.event.service.HumitureRecordService;
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
 * @Date 2021/5/17 下午9:17
 **/
@Service
public class HumitureRecordServiceImpl implements HumitureRecordService {

    @Autowired
    private HumitureRecordDao humitureRecordDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DubboReference
    private TagExposeService tagExposeService;

    @Override
    public void save(HumitureRecord humitureRecord) {
        humitureRecordDao.save(humitureRecord);
    }

    @Override
    public IPageResultData<List<ListHumitureRecordVo>> temperatureHumidityList(Long regionId, Long departmentId, String deviceCode, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Map<String, Object> searchParameter = new HashMap<>();
        if (StringUtils.hasText(deviceCode)){
            searchParameter.put(SearchConstant.LIKE+"_deviceCode","%"+deviceCode+"%");
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
        if (Objects.nonNull(regionId)){
            criteria.and("ri").is(regionId);
        }
        if (Objects.nonNull(departmentId)){
            criteria.and("di").is(departmentId);
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
        List<HumitureRecord> items = mongoTemplate.find(query, HumitureRecord.class);
        List<ListHumitureRecordVo> returnList = new ArrayList<>();
        items.forEach(humitureRecord -> {
            ListHumitureRecordVo vo = new ListHumitureRecordVo();
            BeanUtils.copyProperties(humitureRecord,vo);
            Tag tag = tagExposeService.findById(humitureRecord.getTi());
            if (Objects.nonNull(tag)){
                vo.setTagCode(tag.getTagCode());
                vo.setDeviceName(tag.getDeviceName());
                vo.setDeviceCode(tag.getDeviceCode());
                vo.setType(Type.instance(humitureRecord.getTyp()));
            }
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,lionPage,0L);
    }
}