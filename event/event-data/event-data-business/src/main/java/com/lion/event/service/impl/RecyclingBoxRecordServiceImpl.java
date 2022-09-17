package com.lion.event.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.lion.common.utils.BasicDBObjectUtil;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagState;
import com.lion.device.entity.enums.TagType;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.dao.RecyclingBoxRecordDao;
import com.lion.event.entity.RecyclingBoxRecord;
import com.lion.event.entity.vo.ListRecyclingBoxCurrentVo;
import com.lion.event.entity.vo.ListRecyclingBoxRecordVo;
import com.lion.event.service.RecyclingBoxRecordService;
import com.lion.event.utils.ExcelColumn;
import com.lion.event.utils.ExportExcelUtil;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
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

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @Autowired
    private HttpServletResponse response;

    @Override
    public void save(RecyclingBoxRecord recyclingBoxRecord) {
        recyclingBoxRecordDao.save(recyclingBoxRecord);
    }

    @Override
    public IPageResultData<List<ListRecyclingBoxRecordVo>> list(Boolean isDisinfect, TagType tagType, String name, String code, String tagCode, LocalDateTime startDateTime, LocalDateTime endDateTime, Long id, LionPage lionPage) {
//        List<Long> departmentIds = departmentExposeService.responsibleDepartment(null);
        Query query = new Query();
        Criteria criteria = new Criteria();
//        criteria.and("di").in(departmentIds);
        if (Objects.nonNull(isDisinfect)){
            criteria.and("id").is(isDisinfect);
        }
        if (Objects.nonNull(id)){
            criteria.and("rbi").is(id);
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
    public void listExport(Boolean isDisinfect, TagType tagType, String name, String code, String tagCode, LocalDateTime startDateTime, LocalDateTime endDateTime, Long id, LionPage lionPage) throws IOException, IllegalAccessException {
        IPageResultData<List<ListRecyclingBoxRecordVo>> pageResultData = list(isDisinfect,tagType,name,code,tagCode,startDateTime,endDateTime,id,lionPage);
        List<ListRecyclingBoxRecordVo> list = pageResultData.getData();
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("时间", "ddt"));
        excelColumn.add(ExcelColumn.build("回收箱名称", "rbn"));
        excelColumn.add(ExcelColumn.build("回收箱编码", "rbc"));
        excelColumn.add(ExcelColumn.build("标签编码", "tc"));
        excelColumn.add(ExcelColumn.build("进入回收箱时间", "ddt"));
//        excelColumn.add(ExcelColumn.build("标签类型", "tt"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("recyclingBox.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
    }

    @Override
    public IPageResultData<List<ListRecyclingBoxCurrentVo>> recyclingBoxCurrentList(LocalDateTime startPreviousDisinfectDate, LocalDateTime endPreviousDisinfectDate, String name, String code, LionPage lionPage) {
        List<Device> list = deviceExposeService.find(startPreviousDisinfectDate, endPreviousDisinfectDate, name, code);
        List<Long> ids = new ArrayList<Long>();
        list.forEach(device -> {
            ids.add(device.getId());
        });
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject match = new BasicDBObject();
        match = BasicDBObjectUtil.put(match,"$match","id", false);
        if (ids.size()>0) {
            match = BasicDBObjectUtil.put(match, "$match", "rbi", new BasicDBObject("$in",ids));
        }
        pipeline.add(match);
        BasicDBObject group = new BasicDBObject();
        group = BasicDBObjectUtil.put(group,"$group","_id","$rbi");
        group = BasicDBObjectUtil.put(group,"$group","count",new BasicDBObject("$sum",1));
        pipeline.add(group);
        pipeline.add(new BasicDBObject("$skip",lionPage.getPageNumber()*lionPage.getPageNumber()));
        pipeline.add(new BasicDBObject("$limit",lionPage.getPageSize()));
        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("recycling_box_record").aggregate(pipeline);
        List<ListRecyclingBoxCurrentVo> returnList = new ArrayList<>();
        aggregateIterable.forEach(document -> {
            ListRecyclingBoxCurrentVo vo = new ListRecyclingBoxCurrentVo();
            com.lion.core.Optional<Device> optional = deviceExposeService.findById(document.getLong("_id"));
            if (optional.isPresent()) {
                Device device = optional.get();
                vo.setCode(device.getCode());
                vo.setName(device.getName());
                vo.setCount(NumberUtil.isInteger(String.valueOf(document.get("count")))?document.getInteger("count"):0);
                vo.setRecyclingBoxId(device.getId());
                vo.setPreviousDisinfectDate(device.getPreviousDisinfectDate());
                returnList.add(vo);
            }
        });
        return new PageResultData(returnList,lionPage,returnList.size());
    }

    @Override
    public IPageResultData<List<ListRecyclingBoxCurrentVo>> recyclingBoxCurrentTagList(Long id) {
        return null;
    }

    @Override
    public void recyclingBoxCurrentListExport(LocalDateTime startPreviousDisinfectDate, LocalDateTime endPreviousDisinfectDate, String name, String code, LionPage lionPage) throws IOException, IllegalAccessException {
        IPageResultData<List<ListRecyclingBoxCurrentVo>> pageResultData = recyclingBoxCurrentList(startPreviousDisinfectDate,endPreviousDisinfectDate,name,code,lionPage);
        List<ListRecyclingBoxCurrentVo> list = pageResultData.getData();
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("name", "name"));
        excelColumn.add(ExcelColumn.build("code", "code"));
        excelColumn.add(ExcelColumn.build("count", "count"));
        excelColumn.add(ExcelColumn.build("previous disinfect date", "previousDisinfectDate"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("recyclingBox.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
    }

    @Override
    public void disinfect(Long recyclingBoxId) {
//        List<Long> departmentIds = departmentExposeService.responsibleDepartment(null);
        Query query = new Query();
        Criteria criteria = new Criteria();
//        criteria.and("di").in(departmentIds);
        if (Objects.nonNull(recyclingBoxId)) {
            criteria.and("rbi").is(recyclingBoxId);
        }
        criteria.and("ddt").gte(LocalDateTime.now().minusDays(30));
        criteria.and("id").is(false);
        query.addCriteria(criteria);
        List<RecyclingBoxRecord> items = mongoTemplate.find(query, RecyclingBoxRecord.class);
        items.forEach(recyclingBoxRecord -> {
            tagExposeService.updateState(recyclingBoxRecord.getTi(), TagState.NORMAL.getKey());
            Criteria where = new Criteria();
            where.and("_id").is(recyclingBoxRecord.get_id());
            Update update = new Update();
            update.set("id", true);
            mongoTemplate.updateFirst(new Query(where),update,"recycling_box_record");
            deviceExposeService.updateDisinfectDate(recyclingBoxRecord.getTi());
        });

    }
}
