package com.lion.event.service.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.event.dao.WashEventDao;
import com.lion.event.entity.WashEvent;
import com.lion.event.entity.vo.UserWashDetailsVo;
import com.lion.event.entity.vo.ListUserWashMonitorVo;
import com.lion.event.entity.vo.ListWashMonitorVo;
import com.lion.event.service.WashEventService;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.rule.Wash;
import com.lion.manage.entity.rule.WashUser;
import com.lion.manage.entity.work.Work;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.expose.rule.WashExposeService;
import com.lion.manage.expose.rule.WashUserExposeService;
import com.lion.manage.expose.work.WorkExposeService;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:11
 **/
@Service
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

    @DubboReference
    private WashUserExposeService washUserExposeService;

    @DubboReference
    private WashExposeService washExposeService;

    @Override
    public void save(WashEvent washEvent) {
        washEventDao.save(washEvent);
    }

    @Override
    public void updateUadt(String uuid, LocalDateTime uadt ) {
        washEventDao.updateUadt(uuid,uadt);
    }

    @Override
    public ListWashMonitorVo washRatio(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        ListWashMonitorVo listWashMonitorVo = new ListWashMonitorVo();
        //医院所有事件
        List<Document> listHospitalAll = washEventDao.eventCount(startDateTime,endDateTime, false, null, null , null);
        if (Objects.nonNull(listHospitalAll) && listHospitalAll.size()>0){
            Document hospitalAll = listHospitalAll.get(0);
            listWashMonitorVo.setHospital(init("全院合规率",hospitalAll.getDouble("allViolationRatio"),hospitalAll.getDouble("allNoWashRatio"),hospitalAll.getDouble("allNoAlarmRatio")));
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
    public IPageResultData<List<ListUserWashMonitorVo>> userWashRatio(UserType userType, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        List<Document> list = washEventDao.eventCount(startDateTime,endDateTime,null,userType, null , lionPage);
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
            Department department = departmentUserExposeService.findDepartment(userId);
            if (Objects.nonNull(department)){
                vo.setDepartmentName(department.getName());
            }
        }else {
            return null;
        }
        List<Document> userWash = washEventDao.eventCount(startDateTime,endDateTime,null,null, userId , null);
        if (Objects.nonNull(userWash) && userWash.size()>0) {
            vo.setConformance(new BigDecimal(userWash.get(0).getDouble("allNoAlarmRatio")).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        List<WashEvent> list = washEventDao.userWashDetails(userId,startDateTime,endDateTime,lionPage);
        List<UserWashDetailsVo.UserWashEvent> pageList = new ArrayList<>();
        list.forEach(event -> {
            UserWashDetailsVo.UserWashEvent userWashEvent = new UserWashDetailsVo.UserWashEvent();
            userWashEvent.setDateTime(event.getSdt());
            userWashEvent.setDeviceName(event.getDvn());
            userWashEvent.setRegionName(event.getRn());
            userWashEvent.setIsConformance(!event.getIa());
            pageList.add(userWashEvent);
        });
        vo.setUserWashEvent(new PageResultData<>(pageList,lionPage,0L));
        return vo;
    }

    @Override
    public IPageResultData<List<ListUserWashMonitorVo>> userWashConformanceRatio(String userName, Long departmentId, UserType userType, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Map<String,Object> map = workExposeService.find(departmentId,userName,userType,startDateTime,endDateTime,lionPage.getPageNumber(),lionPage.getPageSize());
        Long totalElements = (Long) map.get("totalElements");
        List<Work> list = (List<Work>) map.get("list");
        List<ListUserWashMonitorVo> returnList = new ArrayList<>();
        list.forEach(work -> {
            if (Objects.nonNull(work.getStartWorkTime())) {
                ListUserWashMonitorVo vo = null;
                LocalDateTime localDateTime = Objects.isNull(work.getEndWorkTime()) ? LocalDateTime.of(work.getStartWorkTime().toLocalDate(), LocalTime.MAX) : work.getEndWorkTime();
                List<Document> documentList = washEventDao.eventCount(work.getStartWorkTime(), localDateTime, null, null, work.getUserId(), null);
                if (Objects.nonNull(documentList) && documentList.size() > 0) {
                    Document document = documentList.get(0);
                    vo = init(work.getStartWorkTime(), work.getEndWorkTime(), work.getUserId(), document);
                } else {
                    vo = init(work.getStartWorkTime(), work.getEndWorkTime(), work.getUserId(), null);
                }
                List<WashUser> washUserList = washUserExposeService.find(work.getUserId());
                if (Objects.isNull(washUserList) || washUserList.size() <= 0) {
                    List<Wash> washList = washExposeService.find(true);
                    if (Objects.isNull(washList) || washList.size() <= 0) {
                        if (Objects.nonNull(vo)) {
                            vo.setIsExistWashRule(false);
                        }
                    }
                }
                returnList.add(vo);
            }
        });
        return new PageResultData<>(returnList,lionPage,totalElements);
    }

    private ListUserWashMonitorVo init(LocalDateTime startDateTime, LocalDateTime endDateTime,Long userId,Document document){
        ListUserWashMonitorVo vo = new ListUserWashMonitorVo();
        if (Objects.nonNull(document)) {
            BigDecimal allViolationRatio = new BigDecimal(document.getDouble("allViolationRatio")).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal allNoWashRatio = new BigDecimal(document.getDouble("allNoWashRatio")).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal allNoAlarmRatio = new BigDecimal(document.getDouble("allNoAlarmRatio")).setScale(2, BigDecimal.ROUND_HALF_UP);
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
        ratio.setViolation(new BigDecimal(violation).setScale(2, BigDecimal.ROUND_HALF_UP));
        ratio.setNoWash(new BigDecimal(noWash).setScale(2, BigDecimal.ROUND_HALF_UP));
        ratio.setConformance(new BigDecimal(conformance).setScale(2, BigDecimal.ROUND_HALF_UP));
        return ratio;
    }

}
