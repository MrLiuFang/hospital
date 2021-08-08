package com.lion.event.service.impl;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagState;
import com.lion.device.entity.enums.TagType;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.dao.RecyclingBoxRecordDao;
import com.lion.event.entity.RecyclingBoxRecord;
import com.lion.event.entity.vo.ListRecyclingBoxRecordVo;
import com.lion.event.service.RecyclingBoxRecordService;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.utils.CurrentUserUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午10:50
 */
@Service
public class RecyclingBoxRecordServiceImpl implements RecyclingBoxRecordService {

    @Autowired
    private RecyclingBoxRecordDao recyclingBoxRecordDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @Override
    public void save(RecyclingBoxRecord recyclingBoxRecord) {
        recyclingBoxRecordDao.save(recyclingBoxRecord);
    }

    @Override
    public IPageResultData<List<ListRecyclingBoxRecordVo>> list(Boolean isDisinfect, TagType tagType, String name, String code, String tagCode, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
//        List<Long> departmentIds = departmentExposeService.responsibleDepartment(null);
        Query query = new Query();
        Criteria criteria = new Criteria();
//        criteria.and("di").in(departmentIds);
        if (Objects.nonNull(isDisinfect)){
            criteria.and("id").is(isDisinfect);
        }
        if (Objects.nonNull(tagType)){
            criteria.and("tt").is(tagType.getKey());
        }
        if (StringUtils.hasText(name)){
            criteria.and("rbn").regex(".*" + name + ".*");
        }
        if (StringUtils.hasText(code)){
            criteria.and("rbc").regex(".*" + code + ".*");
        }
        if (StringUtils.hasText(tagCode)){
            criteria.and("tc").regex(".*" + tagCode + ".*");
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
        List<RecyclingBoxRecord> items = mongoTemplate.find(query, RecyclingBoxRecord.class);
        List<ListRecyclingBoxRecordVo> returnList = new ArrayList<>();
        items.forEach(recyclingBoxRecord -> {
            ListRecyclingBoxRecordVo vo = new ListRecyclingBoxRecordVo();
            BeanUtils.copyProperties(recyclingBoxRecord,vo);
            if (Objects.nonNull(recyclingBoxRecord.getTt())) {
                vo.setTagType(TagType.instance(recyclingBoxRecord.getTt()));
            }
            if (Objects.nonNull(recyclingBoxRecord.getTp())) {
                vo.setTagPurpose(TagPurpose.instance(recyclingBoxRecord.getTp()));
            }
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,lionPage,0L);
    }

    @Override
    public void disinfect() {
        List<Long> departmentIds = departmentExposeService.responsibleDepartment(null);
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("di").in(departmentIds);
        criteria.and("id").is(false);
        query.addCriteria(criteria);
        List<RecyclingBoxRecord> items = mongoTemplate.find(query, RecyclingBoxRecord.class);
        items.forEach(recyclingBoxRecord -> {
            tagExposeService.updateState(recyclingBoxRecord.getTi(), TagState.NORMAL.getKey());
        });

        Criteria where = new Criteria();
        where.and("di").in(departmentIds);
        Update update = new Update();
        update.set("id", true);
        mongoTemplate.updateMulti(new Query(where),update,"recycling_box_record");
    }
}
