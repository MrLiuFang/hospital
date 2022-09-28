package com.lion.event.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.dto.UpdatePositionLeaveTimeDto;
import com.lion.common.enums.Type;
import com.lion.common.expose.file.FileExposeService;
import com.lion.common.utils.BasicDBObjectUtil;
import com.lion.common.utils.RedisUtil;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.Optional;
import com.lion.core.PageResultData;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.dao.PositionDao;
import com.lion.event.entity.Position;
import com.lion.event.entity.dto.EventRecordAddDto;
import com.lion.event.entity.vo.ListPositionVo;
import com.lion.event.entity.vo.ListVisitorVo;
import com.lion.event.service.CurrentPositionService;
import com.lion.event.service.PositionService;
import com.lion.event.utils.ExcelColumn;
import com.lion.event.utils.ExportExcelUtil;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.event.vo.EventRecordVo;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.event.EventRecordExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.expose.person.PatientExposeService;
import com.lion.person.expose.person.TemporaryPersonExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.MessageI18nUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/1 下午6:12
 **/
@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    private PositionDao positionDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CurrentPositionService currentPositionService;

    @DubboReference
    private TagExposeService tagExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private EventRecordExposeService eventRecordExposeService;

    @DubboReference
    private AssetsExposeService assetsExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private PatientExposeService patientExposeService;

    @DubboReference
    private TemporaryPersonExposeService temporaryPersonExposeService;

    @DubboReference
    private RegionExposeService regionExposeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HttpServletResponse response;

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
            criteria.andOperator( Criteria.where("ddt").gte(startDateTime));
        }else if (Objects.isNull(startDateTime) &&  Objects.nonNull(endDateTime)) {
            criteria.andOperator( Criteria.where("ddt").lte(endDateTime));
        }
        query.addCriteria(criteria);
        query.with(lionPage);
        query.with(Sort.by(Sort.Direction.DESC,"ddt"));
        List<Position> items = mongoTemplate.find(query,Position.class);
//        long count = mongoTemplate.count(query, DeviceData.class);
//        PageableExecutionUtils.getPage(items, lionPage, () -> count);
        IPageResultData<List<Position>> pageResultData =new PageResultData<>(items,lionPage,0L);
        List<Position> list = pageResultData.getData();
        list.forEach(position -> {
            Region region = redisUtil.getRegionById(position.getRi());
            if (Objects.nonNull(region)) {
                position.setRn(region.getName());
                Build build = redisUtil.getBuild(region.getBuildId());
                if (Objects.nonNull(build)) {
                    position.setBui(build.getId());
                    position.setBun(build.getName());
                }
                BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
                if (Objects.nonNull(buildFloor)) {
                    position.setBfi(buildFloor.getId());
                    position.setBfn(buildFloor.getName());
                }
            }
        });
        return new PageResultData<>(list,lionPage,0L);

//        return pageResultData;
    }

    public IPageResultData<List<ListPositionVo>> tagPosition(TagPurpose tagPurpose, Long regionId, Long departmentId, String deviceName, String tagCode, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Map<String, Object> searchParameter = new HashMap<>();
        if (Objects.nonNull(tagPurpose)){
            searchParameter.put(SearchConstant.EQUAL+"_purpose",tagPurpose);
        }
        if (StringUtils.hasText(deviceName)){
            searchParameter.put(SearchConstant.LIKE+"_deviceName",deviceName);
        }
        if (StringUtils.hasText(tagCode)){
            searchParameter.put(SearchConstant.LIKE+"_tagCode",tagCode);
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
        if (Objects.nonNull(departmentId)) {
            criteria.and("di").is(departmentId);
        }
        if (Objects.nonNull(regionId)) {
            criteria.and("ri").is(regionId);
        }
        query.addCriteria(criteria);
        query.with(lionPage);
        query.with(Sort.by(Sort.Direction.DESC,"ddt"));
        List<Position> items = mongoTemplate.find(query,Position.class);
        List<ListPositionVo> returnList = new ArrayList<>();
        items.forEach(position -> {
            ListPositionVo vo = new ListPositionVo();
            com.lion.core.Optional<Tag> optionalTag = tagExposeService.findById(position.getTi());
            BeanUtils.copyProperties(position,vo);
            if (optionalTag.isPresent()){
                Tag tag = optionalTag.get();
//                vo.setDeviceName(tag.);
                vo.setTagCode(tag.getTagCode());
                vo.setTagPurpose(tag.getPurpose());
                com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(tag.getDepartmentId());
                if (optionalDepartment.isPresent()) {
                    vo.setDepartmentName(optionalDepartment.get().getName());
                }
            }
            Optional<Region> regionOptional = regionExposeService.findById(vo.getRi());
            if (regionOptional.isPresent()) {
                Region region = regionOptional.get();
                redisTemplate.opsForValue().set(RedisConstants.REGION+region.getId(),region,5, TimeUnit.MINUTES);
                if (Objects.nonNull(region)) {
                    vo.setRn(region.getName());
                    Build build = redisUtil.getBuild(region.getBuildId());
                    if (Objects.nonNull(build)) {
                        vo.setBui(build.getId());
                        vo.setBun(build.getName());
                    }
                    BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
                    if (Objects.nonNull(buildFloor)) {
                        vo.setBfi(buildFloor.getId());
                        vo.setBfn(buildFloor.getName());
                    }
                }
            }
            returnList.add(vo);
        });


        return new PageResultData(returnList,lionPage,0L);
    }

    @Override
    public IPageResultData<List<ListVisitorVo>> regionVisitor(List<Type> types, Long regionId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.isNull(types) || types.size()<=0) {
            types =new ArrayList<>();
            types.add(Type.ASSET);
            types.add(Type.STAFF);
            types.add(Type.MIGRANT);
            types.add(Type.PATIENT);
        }

        if (Objects.nonNull(types) && types.size()>0) {
            List<Integer> typ = new ArrayList<>();
            types.forEach(type -> {
                typ.add(type.getKey());
            });
            criteria.and("typ").in(typ);
        }
        if (Objects.nonNull(regionId)) {
            criteria.and("ri").is(regionId);
        }
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(30);
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            criteria.andOperator( Criteria.where("ddt").gte(startDateTime) ,Criteria.where("ddt").lte(endDateTime));
        }else if (Objects.nonNull(startDateTime) &&  Objects.isNull(endDateTime)) {
            criteria.andOperator( Criteria.where("ddt").gte(startDateTime));
        }else if (Objects.isNull(startDateTime) &&  Objects.nonNull(endDateTime)) {
            criteria.andOperator( Criteria.where("ddt").lte(endDateTime));
        }
        query.addCriteria(criteria);
        query.with(lionPage);
        query.with(Sort.by(Sort.Direction.DESC,"ddt"));
        List<Position> items = mongoTemplate.find(query,Position.class);
//        long count = mongoTemplate.count(query, DeviceData.class);
//        PageableExecutionUtils.getPage(items, lionPage, () -> count);
        List<ListVisitorVo> returnList = new ArrayList<ListVisitorVo>();
        items.forEach(position -> {
            ListVisitorVo vo = new ListVisitorVo();
            BeanUtils.copyProperties(position,vo);
            Type type = Type.instance(position.getTyp());
            Long img = null;
            if (Objects.equals(type,Type.ASSET)) {
                com.lion.core.Optional<Assets> optionalAssets = assetsExposeService.findById(position.getAdi());
                if (optionalAssets.isPresent()){
                    vo.setName(optionalAssets.get().getName());
                }
                img = optionalAssets.get().getImg();
            }else if (Objects.equals(type,Type.STAFF)) {
                com.lion.core.Optional<User> optionalUser = userExposeService.findById(position.getPi());
                if (optionalUser.isPresent()) {
                    vo.setName(optionalUser.get().getName());
                }
            }else if (Objects.equals(type,Type.PATIENT)) {
                com.lion.core.Optional<Patient> optionalPatient = patientExposeService.findById(position.getPi());
                if (optionalPatient.isPresent()) {
                    vo.setName(optionalPatient.get().getName());
                }
            }else if (Objects.equals(type,Type.MIGRANT)) {
                com.lion.core.Optional<TemporaryPerson> optionalTemporaryPerson = temporaryPersonExposeService.findById(position.getPi());
                if (optionalTemporaryPerson.isPresent()) {
                    vo.setName(optionalTemporaryPerson.get().getName());
                }
            }


            if (Objects.nonNull(img)) {
                vo.setImg(img);
                vo.setImgUrl(fileExposeService.getUrl(img));
            }
            Optional<Tag> tagOptional = tagExposeService.findById(position.getTi());
            if (tagOptional.isPresent()) {
                vo.setTagCode(tagOptional.get().getTagCode());
            }
            returnList.add(vo);
        });
        IPageResultData<List<ListVisitorVo>> pageResultData =new PageResultData<>(returnList,lionPage,0L);
        return pageResultData;
    }

    @Override
    public void positionExport(Long pi, Long ri, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage, HttpServletResponse response, HttpServletRequest request) throws IOException, IllegalAccessException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.parse("2000-01-01 00:00:00", dateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        IPageResultData<List<Position>> pageResultData = list(pi,null,ri,startDateTime,endDateTime,lionPage);
        List<Position> list = pageResultData.getData();
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build(MessageI18nUtil.getMessage("3000036"), "rn"));
        excelColumn.add(ExcelColumn.build(MessageI18nUtil.getMessage("3000037"), "ddt"));
        excelColumn.add(ExcelColumn.build(MessageI18nUtil.getMessage("3000038"), "ldt"));
        excelColumn.add(ExcelColumn.build(MessageI18nUtil.getMessage("3000039"), "t"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("position.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
//        List<Map<String,String>> searchCriteria = new ArrayList<Map<String,String>>();
//        if (Objects.nonNull(startDateTime)) {
//            Map<String,String> map = new HashMap<String,String>();
//            map.put("name","开始时间");
//            map.put("key","startDateTime");
//            map.put("value",dateTimeFormatter.format(startDateTime));
//            searchCriteria.add(map);
//        }
//        if (Objects.nonNull(endDateTime)) {
//            Map<String,String> map = new HashMap<String,String>();
//            map.put("name","结束时间");
//            map.put("key","endDateTime");
//            map.put("value",dateTimeFormatter.format(endDateTime));
//            searchCriteria.add(map);
//        }
//        if (Objects.nonNull(pi)) {
//            Map<String,String> map = new HashMap<String,String>();
//            map.put("name","人员id");
//            map.put("key","personId");
//            map.put("value", String.valueOf(pi));
//            searchCriteria.add(map);
//        }
//        if (Objects.nonNull(ri)) {
//            Map<String,String> map = new HashMap<String,String>();
//            map.put("name","区域id");
//            map.put("key","regionId");
//            map.put("value", String.valueOf(ri));
//            searchCriteria.add(map);
//        }

    }

    @Override
    public void eventRecordAdd(EventRecordAddDto eventRecordAddDto, HttpServletRequest request) throws JsonProcessingException {
        String code = eventRecordAddDto.getCode();
        String remarks = eventRecordAddDto.getRemarks();
        eventRecordExposeService.add(code,remarks,"轨迹导出",eventRecordAddDto.getExtend(),request.getServletPath());
    }

    @Override
    public List<String> personAllRegion(Long personId, Long regionId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return positionDao.personAllRegion(personId,regionId , startDateTime, endDateTime);
    }
    @Override
    public void tagPositionExport(TagPurpose tagPurpose, Long regionId, Long departmentId, String deviceName, String tagCode, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) throws IOException, IllegalAccessException {
        IPageResultData<List<ListPositionVo>> pageResultData = tagPosition(tagPurpose,regionId,departmentId,deviceName,tagCode,startDateTime,endDateTime,lionPage);
        List<ListPositionVo> list = pageResultData.getData();
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("datetime", "ddt"));
        excelColumn.add(ExcelColumn.build("tag purpose", "tagPurpose"));
        excelColumn.add(ExcelColumn.build("tag code", "tagCode"));
        excelColumn.add(ExcelColumn.build("device name", "deviceName"));
        excelColumn.add(ExcelColumn.build("department name", "departmentName"));
        excelColumn.add(ExcelColumn.build("region", "rn"));
        excelColumn.add(ExcelColumn.build("entry time", "ddt"));
        excelColumn.add(ExcelColumn.build("departure time", "ldt"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("tagPosition.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
    }

    @Override
    public void updatePositionLeaveTime(UpdatePositionLeaveTimeDto dto) {
        Document match = new Document();
        match.put("pi",dto.getPi());
        match.put("ddt",dto.getPddt());
        match.put("sdt",dto.getPsdt());
        Query query = new BasicQuery(match);
        Position position = mongoTemplate.findOne(query, Position.class);
        if (Objects.nonNull(position)) {
            Query queryUpdate = new Query();
            queryUpdate.addCriteria(Criteria.where("_id").is(position.get_id()));
            Update update = new Update();
            Duration duration = Duration.between(dto.getPddt(),dto.getCddt());
            update.set("t", Integer.valueOf(String.valueOf(duration.toMinutes())));
            update.set("ldt", dto.getCddt());
            mongoTemplate.updateFirst(queryUpdate, update, "position");
        }
    }

    @Override
    public IPageResultData<List<EventRecordVo>> eventRecordList(String code, String name, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        return eventRecordExposeService.list(endDateTime,endDateTime,code,name,lionPage);
    }

    @Override
    public void eventRecordListExport(String code, String name, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) throws IOException, IllegalAccessException {
        IPageResultData<List<EventRecordVo>> pageResultData = eventRecordList(code, name, startDateTime, endDateTime, lionPage);
        List<EventRecordVo> list = pageResultData.getData();
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("code", "code"));
        excelColumn.add(ExcelColumn.build("name", "createUserName"));
        excelColumn.add(ExcelColumn.build("datetime", "createDateTime"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("eventRecord.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
    }

    @Override
    public EventRecordVo eventRecordDetails(Long id) {
        return eventRecordExposeService.details(id);
    }

    @Override
    public int count(Type type, Long ri, Long buildFloorId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Bson> pipeline = new ArrayList<Bson>();
        BasicDBObject match = new BasicDBObject();
        if (Objects.nonNull(buildFloorId)) {
            match = BasicDBObjectUtil.put(match, "$match", "bfi", new BasicDBObject("$eq", buildFloorId));
        }
        match = BasicDBObjectUtil.put(match,"$match","ddt", new BasicDBObject("$gte",startDateTime).append("$lte",endDateTime));
        match = BasicDBObjectUtil.put(match,"$match","ri",new BasicDBObject("$eq",ri) );
        match = BasicDBObjectUtil.put(match,"$match","typ",new BasicDBObject("$eq",type.getKey()) );
        pipeline.add(match);
        BasicDBObject group = new BasicDBObject();
        if (Objects.equals(type,Type.ASSET)) {
            group = BasicDBObjectUtil.put(group, "$group", "_id", "$adi");
        }else {
            group = BasicDBObjectUtil.put(group, "$group", "_id", "$pi");
        }
        group = BasicDBObjectUtil.put(group,"$group","count",new BasicDBObject("$sum",1));
        pipeline.add(group);
        AggregateIterable<Document> aggregateIterable = mongoTemplate.getCollection("position").aggregate(pipeline);
        AtomicInteger count = new AtomicInteger(0);
        aggregateIterable.forEach(document -> {
            if (document.containsKey("_id")) {
                count.set(count.get()+1);
            }
        });
        return count.get();
    }
}
