package com.lion.event.service.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagRuleEffect;
import com.lion.device.entity.enums.TagType;
import com.lion.event.dao.UserTagButtonRecordDao;
import com.lion.event.entity.RecyclingBoxRecord;
import com.lion.event.entity.UserTagButtonRecord;
import com.lion.event.entity.vo.ListRecyclingBoxRecordVo;
import com.lion.event.entity.vo.ListUserTagButtonRecordVo;
import com.lion.event.service.UserTagButtonRecordService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/15 下午3:36
 */
@Service
public class UserTagButtonRecordServiceImpl implements UserTagButtonRecordService {

    @Autowired
    private UserTagButtonRecordDao userTagButtonRecordDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Override
    public void add(UserTagButtonRecord userTagButtonRecord) {
        userTagButtonRecordDao.save(userTagButtonRecord);
    }

    @Override
    public IPageResultData<List<ListUserTagButtonRecordVo>> list(TagRuleEffect tagRuleEffect, String name, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(tagRuleEffect)){
            criteria.and("bi").is(tagRuleEffect.getKey());
        }
        if (StringUtils.hasText(name)){
            criteria.and("n").regex(".*" + name + ".*");
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
        List<UserTagButtonRecord> items = mongoTemplate.find(query, UserTagButtonRecord.class);
        List<ListUserTagButtonRecordVo> returnList = new ArrayList<>();
        items.forEach(userTagButtonRecord -> {
            ListUserTagButtonRecordVo vo = new ListUserTagButtonRecordVo();
            BeanUtils.copyProperties(userTagButtonRecord,vo);
            User user = userExposeService.findById(userTagButtonRecord.getPi());
            if (Objects.nonNull(user)){
                vo.setHeadPortrait(user.getHeadPortrait());
                vo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,lionPage,0L);
    }
}
