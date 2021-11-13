package com.lion.event.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lion.common.dto.UserLastWashDto;
import com.lion.common.enums.WashEventType;
import com.lion.common.expose.file.FileExposeService;
import com.lion.common.utils.RedisUtil;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.device.entity.device.Device;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.device.DeviceGroupDeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.dao.WashEventDao;
import com.lion.event.entity.WashEvent;
import com.lion.event.entity.vo.*;
import com.lion.event.service.WashEventService;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.manage.expose.work.WorkExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.UserType;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.upms.expose.user.UserTypeExposeService;
import com.lion.utils.CurrentUserUtil;
import com.lion.utils.MessageI18nUtil;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/1 下午6:11
 **/
@Service
@Log
public class WashEventServiceImpl implements WashEventService {

    @Autowired
    private WashEventDao washEventDao;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @DubboReference
    private WorkExposeService workExposeService;

//    @DubboReference
//    private WashUserExposeService washUserExposeService;
//
//    @DubboReference
//    private WashExposeService washExposeService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @Autowired
    private HttpServletResponse response;

    @DubboReference
    private RegionExposeService regionExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private DeviceGroupDeviceExposeService deviceGroupDeviceExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @DubboReference
    private CctvExposeService cctvExposeService;

    @DubboReference
    private AssetsExposeService assetsExposeService;

    @DubboReference
    private UserTypeExposeService userTypeExposeService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisUtil redisUtil;

    private final String FONT = "simsun.ttc";

    @Override
    public void save(WashEvent washEvent) {
        washEventDao.save(washEvent);
    }

    @Override
    public void updateWt(String uuid, LocalDateTime uadt ) {
        washEventDao.updateWt(uuid,uadt);
    }

    @Override
    public ListWashMonitorVo washRatio(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        ListWashMonitorVo listWashMonitorVo = new ListWashMonitorVo();
        //医院所有事件
        List<Document> listHospitalAll = washEventDao.eventCount(startDateTime,endDateTime, false, null, null , null);
        if (Objects.nonNull(listHospitalAll) && listHospitalAll.size()>0){
            Document hospitalAll = listHospitalAll.get(0);
            listWashMonitorVo.setHospital(init(MessageI18nUtil.getMessage("3000001"),hospitalAll.getDouble("allViolationRatio"),hospitalAll.getDouble("allNoWashRatio"),hospitalAll.getDouble("allNoAlarmRatio")));
        }
        //所有科室事件
        List<Document> listDepartmentAll = washEventDao.eventCount(startDateTime,endDateTime,true,null, null , null);
        List<ListWashMonitorVo.Ratio> list = new ArrayList<>();
        listDepartmentAll.forEach(document -> {
            list.add(init(document.getString("_id"),document.getDouble("allViolationRatio"),document.getDouble("allNoWashRatio"),document.getDouble("allNoAlarmRatio")));
        });
        listWashMonitorVo.setDepartment(list);
        return listWashMonitorVo;
    }

    @Override
    public IPageResultData<List<ListUserWashMonitorVo>> userWashRatio(Long userTypeId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        List<Document> list = washEventDao.eventCount(startDateTime,endDateTime,null,userTypeId, null , lionPage);
        List<ListUserWashMonitorVo> returnList = new ArrayList<>();
        list.forEach(document -> {
            returnList.add(init(null,null,document.getLong("_id"),document));
        });
        return new PageResultData<>(returnList,lionPage,0L);
    }

    @Override
    public UserWashDetailsVo userWashDetails(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        UserWashDetailsVo vo = new UserWashDetailsVo();
        User user = userExposeService.findById(userId);
        if (Objects.nonNull(user)){
            BeanUtils.copyProperties(user,vo);
            vo.setHeadPortrait(user.getHeadPortrait());
            vo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            vo.setUserType(userTypeExposeService.findById(user.getUserTypeId()));
            Department department = departmentUserExposeService.findDepartment(userId);
            if (Objects.nonNull(department)){
                vo.setDepartmentName(department.getName());
            }
        }else {
            return null;
        }
        List<Document> userWash = washEventDao.eventCount(startDateTime,endDateTime,null,null, userId , null);
        if (Objects.nonNull(userWash) && userWash.size()>0) {
            vo.setConformance(new BigDecimal(userWash.get(0).getDouble("allNoAlarmRatio")).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)));
        }
        List<WashEvent> list = washEventDao.userWashDetails(userId,startDateTime,endDateTime,lionPage);
        List<UserWashDetailsVo.UserWashEvent> pageList = new ArrayList<>();
        list.forEach(event -> {
            UserWashDetailsVo.UserWashEvent userWashEvent = new UserWashDetailsVo.UserWashEvent();
            userWashEvent.setDateTime(event.getAdt());
            userWashEvent.setDeviceName(event.getDvn());
            userWashEvent.setRegionName(event.getRn());
            userWashEvent.setIsConformance(!event.getIa());
            userWashEvent.setTime(event.getT());
            pageList.add(userWashEvent);
        });
        vo.setUserWashEvent(pageList);
        return vo;
    }

    @Override
    public IPageResultData<List<ListUserWashMonitorVo>> userWashConformanceRatio(String userName, List<Long> departmentIds, List<Long> userIds, Long userTypeId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        PageResultData<Map<String,Object>> page = workExposeService.find(departmentIds,userIds , userName, userTypeId, startDateTime, endDateTime, lionPage);
        Long totalElements = (Long) page.getTotalElements();
        List<Map<String,Object>> list = page.getContent();
        List<ListUserWashMonitorVo> returnList = new ArrayList<>();
        list.forEach(map -> {
            ListUserWashMonitorVo vo = null;
            LocalDateTime startWorkTime = Objects.isNull(map.get("start_work_time"))?null: (LocalDateTime) map.get("start_work_time");
            LocalDateTime endWorkTime = Objects.isNull(map.get("end_work_time"))?null: (LocalDateTime) map.get("end_work_time");
            Long userId = Long.valueOf(String.valueOf(map.get("id")) );
            if (Objects.nonNull(startWorkTime)) {
                List<Document> documentList = washEventDao.eventCount(startWorkTime, endWorkTime, null, null, userId, null);
                if (Objects.nonNull(documentList) && documentList.size() > 0) {
                    Document document = documentList.get(0);
                    vo = init(startWorkTime, endWorkTime, userId, document);
                } else {
                    vo = init(startWorkTime, endWorkTime, userId, null);
                }
            }else {
                vo = init(startWorkTime, endWorkTime, userId, null);
            }
//            List<WashUser> washUserList = washUserExposeService.find(userId);
//            if (Objects.isNull(washUserList) || washUserList.size() <= 0) {
//                List<Wash> washList = washExposeService.findLoopWash(true);
//                if (Objects.isNull(washList) || washList.size() <= 0) {
//                    if (Objects.nonNull(vo)) {
//                        vo.setIsExistWashRule(false);
//                    }
//                }
//            }
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,lionPage,totalElements);
    }

    @Override
    public void userWashConformanceRatioExport(String userName, List<Long> departmentIds, List<Long> userIds, Long userTypeId, LocalDateTime startDateTime, LocalDateTime endDateTime) throws DocumentException, IOException {
        IPageResultData<List<ListUserWashMonitorVo>> page = userWashConformanceRatio(userName, departmentIds,userIds , userTypeId, startDateTime, endDateTime, new LionPage(0,Integer.MAX_VALUE));
        List<ListUserWashMonitorVo> list = page.getData();
        BaseFont bfChinese = BaseFont.createFont(FONT+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font fontChinese = new Font(bfChinese);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(MessageI18nUtil.getMessage("3000028")+".pdf", "UTF-8"));
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        pageSize.rotate();
        document.setPageSize(pageSize);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, servletOutputStream);
        writer.setPageEvent(new PdfPageEventHelper(FONT,CurrentUserUtil.getCurrentUserUsername()));
        document.open();
        PdfPTable table = new PdfPTable(8);
        table.setWidths(new int[]{10, 10, 10, 20, 20, 10, 10, 10});
        table.setWidthPercentage(100);
        PdfPCell cellTitle = new PdfPCell(new Paragraph(MessageI18nUtil.getMessage("3000028"), new Font(bfChinese,24)));
        cellTitle.setColspan(8);
        cellTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellTitle);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PdfPCell cellTitle1 = new PdfPCell(new Paragraph(MessageI18nUtil.getMessage("3000003")+":" +simpleDateFormat.format(new Date()), new Font(bfChinese)));
        cellTitle1.setColspan(8);
        table.addCell(cellTitle1);
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000004"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000006"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000029"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000030"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000031"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000032"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000033"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000034"), fontChinese));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (ListUserWashMonitorVo vo : list) {
            table.addCell(new Paragraph(vo.getUserName(), fontChinese));
            table.addCell(new Paragraph(vo.getDepartmentName(), fontChinese));
            User user = userExposeService.findById(vo.getUserId());
            UserType userType = null;
            if (Objects.nonNull(user)) {
                userType = userTypeExposeService.findById(user.getUserTypeId());
            }
            table.addCell(new Paragraph((Objects.nonNull(user)&&Objects.nonNull(userType))?userType.getUserTypeName():"", fontChinese));
            table.addCell(new Paragraph(Objects.nonNull(vo.getStartWorkTime())?dateTimeFormatter.format(vo.getStartWorkTime()):"", fontChinese));
            table.addCell(new Paragraph(Objects.nonNull(vo.getEndWorkTime())?dateTimeFormatter.format(vo.getEndWorkTime()):"", fontChinese));
            table.addCell(new Paragraph(String.valueOf(vo.getConformance()), fontChinese));
            table.addCell(new Paragraph(String.valueOf(vo.getViolation()), fontChinese));
            table.addCell(new Paragraph(String.valueOf(vo.getNoWash()), fontChinese));
        }
        document.add(table);
        document.close();
        servletOutputStream.flush();
        servletOutputStream.close();
    }

    @Override
    public IPageResultData<List<ListWashEventVo>> listWashEvent(Boolean ia, Long userTypeId, WashEventType type, Long regionId, Long departmentId, List<Long> userIds, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(ia)) {
            criteria.and("ia").is(ia);
        }
        if (Objects.nonNull(userTypeId)) {
            criteria.and("py").is(userTypeId);
        }
        if (Objects.nonNull(type)) {
            criteria.and("ai").is(ia);
        }
        if (Objects.nonNull(type)){
            criteria.and("wet").is(type.getKey());
        }
        if (Objects.nonNull(regionId)){
            criteria.and("ri").is(regionId);
        }
        if (Objects.nonNull(departmentId)){
            criteria.and("di").is(departmentId);
        }
        if (Objects.nonNull(userIds) && userIds.size()>0){
            criteria.and("pi").in(userIds);
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
        List<WashEvent> items = mongoTemplate.find(query,WashEvent.class);
//        long count = mongoTemplate.count(query, WashEvent.class);
//        PageableExecutionUtils.getPage(items, lionPage, () -> count);
        List<ListWashEventVo> returnList = new ArrayList<>();
        items.forEach(washEvent -> {
            ListWashEventVo vo = new ListWashEventVo();
            User user = userExposeService.findById(washEvent.getPi());
            if (Objects.nonNull(user)){
                vo.setUserType(userTypeExposeService.findById(user.getUserTypeId()));
                vo.setName(user.getName());
                vo.setNumber(user.getNumber());
                vo.setGender(user.getGender());
            }
            vo.setDepartmentName(washEvent.getDn());
            vo.setIa(washEvent.getIa());
            vo.setTime(washEvent.getT());
            vo.setUseDateTime(washEvent.getDdt());
            Device device = deviceExposeService.findById(washEvent.getDvi());
            if (Objects.nonNull(device)){
                vo.setDeviceName(device.getName());
            }
            returnList.add(vo);
        });
        IPageResultData<List<ListWashEventVo>> pageResultData =new PageResultData<>(returnList,lionPage,0L);
        return pageResultData;
    }

    @Override
    public void listWashEventExport(Boolean ia, WashEventType type, Long regionId, Long departmentId, List<Long> userIds, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) throws IOException, DocumentException {
        IPageResultData<List<ListWashEventVo>> pageResultData = listWashEvent(ia, null , type, regionId, departmentId, userIds, startDateTime, endDateTime, lionPage);
        List<ListWashEventVo> list = pageResultData.getData();
        BaseFont bfChinese = BaseFont.createFont(FONT+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font fontChinese = new Font(bfChinese);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(MessageI18nUtil.getMessage("3000002")+".pdf", "UTF-8"));
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        pageSize.rotate();
        document.setPageSize(pageSize);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, servletOutputStream);
        String userName = CurrentUserUtil.getCurrentUserUsername();
        writer.setPageEvent(new PdfPageEventHelper(FONT,userName));
        document.open();
        PdfPTable table = new PdfPTable(8);
        table.setWidths(new int[]{10, 10, 10, 10, 10, 20, 20, 10});
        table.setWidthPercentage(100);
        PdfPCell cellTitle = new PdfPCell(new Paragraph(MessageI18nUtil.getMessage("3000002"), new Font(bfChinese,24)));
        cellTitle.setColspan(8);
        cellTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellTitle);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PdfPCell cellTitle1 = new PdfPCell(new Paragraph(MessageI18nUtil.getMessage("3000003")+":" +simpleDateFormat.format(new Date()), new Font(bfChinese)));
        cellTitle1.setColspan(8);
        table.addCell(cellTitle1);
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000004"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000005"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000006"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000007"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000008"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000009"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000010"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000011"), fontChinese));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (ListWashEventVo listWashEventVo : list) {
            table.addCell(new Paragraph(listWashEventVo.getName(), fontChinese));
            table.addCell(new Paragraph(Objects.isNull(listWashEventVo.getNumber())?"":String.valueOf(listWashEventVo.getNumber()), fontChinese));
            table.addCell(new Paragraph(listWashEventVo.getDepartmentName(), fontChinese));
            table.addCell(new Paragraph(Objects.isNull(listWashEventVo.getUserType())?"":listWashEventVo.getUserType().getUserTypeName(), fontChinese));
            table.addCell(new Paragraph(Objects.isNull(listWashEventVo.getGender())?"":listWashEventVo.getGender().getDesc(), fontChinese));
            table.addCell(new Paragraph(listWashEventVo.getDeviceName(), fontChinese));
            table.addCell(new Paragraph(Objects.isNull(listWashEventVo.getUseDateTime())?"":dateTimeFormatter.format(listWashEventVo.getUseDateTime()), fontChinese));
            table.addCell(new Paragraph(Objects.equals(listWashEventVo.getIa(),true)?MessageI18nUtil.getMessage("3000012"):MessageI18nUtil.getMessage("3000013"), fontChinese));
        }
        document.add(table);
        document.close();
        servletOutputStream.flush();
        servletOutputStream.close();
    }

    @Override
    public IPageResultData<List<ListWashEventRegionVo>> washEventRegionRatio(Long buildFloorId, Long regionId, Long departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(buildFloorId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_buildFloorId",buildFloorId);
        }
        if (Objects.nonNull(regionId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_regionId",regionId);
        }
        if (Objects.nonNull(departmentId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_departmentId",departmentId);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        PageResultData resultData = regionExposeService.find(lionPage);
        List<Region> list = resultData.getContent();
        List<ListWashEventRegionVo> returnList = new ArrayList<>();
        list.forEach(region -> {
            ListWashEventRegionVo vo = new ListWashEventRegionVo();
            vo.setRegionName(region.getName());
            vo.setRegionId(region.getId());
            Department department = departmentExposeService.findById(region.getDepartmentId());
            vo.setDepartmentName(Objects.isNull(department)?"":department.getName());
//            vo.setDeviceCount(vo.getDeviceCount()+deviceGroupDeviceExposeService.countDevice(region.getDeviceGroupId()));
            Document document = washEventDao.eventCount(startDateTime, endDateTime, region.getId());
            if (Objects.nonNull(document)) {
                vo.setRatio(new BigDecimal(document.getDouble("allNoAlarmRatio")).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,resultData.getPageable(),resultData.getTotalElements());
    }

    @Override
    public void washEventRegionRatioExport(Long buildFloorId, Long regionId, Long departmentId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) throws IOException, DocumentException {
        IPageResultData<List<ListWashEventRegionVo>> pageResultData = washEventRegionRatio(buildFloorId, regionId, departmentId, startDateTime, endDateTime, lionPage);
        List<ListWashEventRegionVo> list = pageResultData.getData();
        BaseFont bfChinese = BaseFont.createFont(FONT+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font fontChinese = new Font(bfChinese);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(MessageI18nUtil.getMessage("3000002")+".pdf", "UTF-8"));
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        pageSize.rotate();
        document.setPageSize(pageSize);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, servletOutputStream);
        String userName = CurrentUserUtil.getCurrentUserUsername();
        writer.setPageEvent(new PdfPageEventHelper(FONT,userName));
        document.open();
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        PdfPCell cellTitle = new PdfPCell(new Paragraph(MessageI18nUtil.getMessage("3000002"), new Font(bfChinese,24)));
        cellTitle.setColspan(4);
        cellTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellTitle);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PdfPCell cellTitle1 = new PdfPCell(new Paragraph(MessageI18nUtil.getMessage("3000003")+": "+simpleDateFormat.format(new Date()), new Font(bfChinese)));
        cellTitle1.setColspan(4);
        table.addCell(cellTitle1);
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000014"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000006"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000015"), fontChinese));
        table.addCell(new Paragraph(MessageI18nUtil.getMessage("3000016"), fontChinese));
        for (ListWashEventRegionVo listWashEventRegionVo : list) {
            table.addCell(new Paragraph(listWashEventRegionVo.getRegionName(), fontChinese));
            table.addCell(new Paragraph(listWashEventRegionVo.getDepartmentName(), fontChinese));
            table.addCell(new Paragraph(String.valueOf(listWashEventRegionVo.getDeviceCount()), fontChinese));
            if (Objects.nonNull(listWashEventRegionVo.getRatio())) {
                table.addCell(new Paragraph(listWashEventRegionVo.getRatio().toString()+"%", fontChinese));
            }else {
                table.addCell(new Paragraph("", fontChinese));
            }
        }
        document.add(table);
        document.close();
        servletOutputStream.flush();
        servletOutputStream.close();
    }

    @Override
    public void updateWashTime(UserLastWashDto userLastWashDto) {
//        if (Objects.nonNull(userLastWashDto) && Objects.nonNull(userLastWashDto.getDateTime()) && Objects.nonNull(userLastWashDto.getUserId()) ) {
//            Document match = new Document();
//            match.put("pi",userLastWashDto.getUserId());
//            match.put("ddt",userLastWashDto.getDateTime());
//            match.put("sdt",userLastWashDto.getSystemDateTime());
//            Query query = new BasicQuery(match);
//            WashEvent washEvent = mongoTemplate.findOne(query, WashEvent.class);
//            if (Objects.nonNull(washEvent)) {
//                Query queryUpdate = new Query();
//                queryUpdate.addCriteria(Criteria.where("_id").is(washEvent.get_id()));
//                Update update = new Update();
//                update.set("t", userLastWashDto.getTime());
//                if (Objects.nonNull(washEvent.getWi())) {
//                    Wash wash = redisUtil.getWashById(washEvent.getWi());
//                    if (Objects.nonNull(wash)) {
//                        if (userLastWashDto.getTime()<wash.getDuration()) {
//                            update.set("ia",true);
//                            update.set("at",SystemAlarmType.WDDBZSXSC.getKey());
//                            // TODO 给硬件发消息
//                            log.info("给硬件发送消息-洗手时长不够");
//                            SystemAlarmDto systemAlarmDto = new SystemAlarmDto();
//                            systemAlarmDto.setDateTime(LocalDateTime.now());
//                            systemAlarmDto.setType(Type.STAFF);
//                            systemAlarmDto.setTagId(userLastWashDto.getTagId());
//                            systemAlarmDto.setRegionId(washEvent.getRi());
//                            systemAlarmDto.setPeopleId(washEvent.getPi());
//                            systemAlarmDto.setDelayDateTime(systemAlarmDto.getDateTime());
//                            systemAlarmDto.setSystemAlarmType(SystemAlarmType.WDDBZSXSC);
//                            try {
//                                rocketMQTemplate.syncSend(TopicConstants.SYSTEM_ALARM, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(systemAlarmDto)).build());
//                            } catch (JsonProcessingException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//                mongoTemplate.updateFirst(queryUpdate, update, "wash_event");
//            }
//        }
    }

    @Override
    public IPageResultData<List<ListViolationWashEventVo>> violationWashEvent(LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(30);
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            criteria.andOperator(Criteria.where("ddt").gte(startDateTime), Criteria.where("ddt").lte(endDateTime));
        }else if (Objects.nonNull(startDateTime)) {
            criteria.and("ddt").gte(startDateTime);
        }else if (Objects.nonNull(endDateTime)) {
            criteria.and("ddt").lte(endDateTime);
        }
        criteria.and("ia").is(true);
        query.addCriteria(criteria);
        query.with(lionPage);
        query.with(Sort.by(Sort.Direction.DESC,"ddt"));
        List<WashEvent> items = mongoTemplate.find(query,WashEvent.class);
        List<ListViolationWashEventVo> returnList = new ArrayList<ListViolationWashEventVo>();
        items.forEach(washEvent -> {
            ListViolationWashEventVo vo = new ListViolationWashEventVo();
            BeanUtils.copyProperties(washEvent,vo);
            User user = userExposeService.findById(washEvent.getPi());
            if (Objects.nonNull(user)) {
                vo.setName(user.getName());
            }
            SystemAlarmType systemAlarmType = SystemAlarmType.instance(washEvent.getAt());
            if (Objects.nonNull(systemAlarmType)) {
                vo.setAlarmType(systemAlarmType);
                vo.setAlarmTypeStr(systemAlarmType.getDesc());
            }
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,lionPage,returnList.size());
    }

    private ListUserWashMonitorVo init(LocalDateTime startDateTime, LocalDateTime endDateTime,Long userId,Document document){
        ListUserWashMonitorVo vo = new ListUserWashMonitorVo();
        if (Objects.nonNull(document)) {
            BigDecimal allViolationRatio = new BigDecimal(document.getDouble("allViolationRatio")).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            BigDecimal allNoWashRatio = new BigDecimal(document.getDouble("allNoWashRatio")).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            BigDecimal allNoAlarmRatio = new BigDecimal(document.getDouble("allNoAlarmRatio")).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            vo.setViolation(allViolationRatio);
            vo.setNoWash(allNoWashRatio);
            vo.setConformance(allNoAlarmRatio);
        }
        vo.setStartWorkTime(startDateTime);
        vo.setEndWorkTime(endDateTime);
        vo.setUserId(userId);
        User user = userExposeService.findById(vo.getUserId());
        if (Objects.nonNull(user)) {
            vo.setUserName(user.getName());
            vo.setHeadPortrait(user.getHeadPortrait());
            vo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            vo.setUserType(userTypeExposeService.findById(user.getUserTypeId()));
            Department department = departmentUserExposeService.findDepartment(vo.getUserId());
            if (Objects.nonNull(department)) {
                vo.setDepartmentName(department.getName());
            }

        }
        return vo;
    }

    private ListWashMonitorVo.Ratio init(String name, Double violation, Double noWash, Double conformance) {
        ListWashMonitorVo.Ratio ratio = new ListWashMonitorVo.Ratio();
        ratio.setName(name);
        ratio.setViolation(new BigDecimal(violation).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
        ratio.setNoWash(new BigDecimal(noWash).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
        ratio.setConformance(new BigDecimal(conformance).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
        return ratio;
    }

}
