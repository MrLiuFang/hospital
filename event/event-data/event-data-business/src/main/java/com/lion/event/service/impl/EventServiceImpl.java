package com.lion.event.service.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.event.dao.EventDao;
import com.lion.event.entity.Event;
import com.lion.event.entity.vo.UserWashDetailsVo;
import com.lion.event.entity.vo.ListUserWashMonitorVo;
import com.lion.event.entity.vo.ListWashMonitorVo;
import com.lion.event.service.EventService;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentUserExposeService;
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
import java.util.*;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:11
 **/
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventDao eventDao;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @Override
    public void save(Event event) {
        eventDao.save(event);
    }

    @Override
    public void updateUadt(String uuid, LocalDateTime uadt ) {
        eventDao.updateUadt(uuid,uadt);
    }

    @Override
    public ListWashMonitorVo washRatio(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        ListWashMonitorVo listWashMonitorVo = new ListWashMonitorVo();
        //医院所有事件
        List<Document> listHospitalAll = eventDao.eventCount(startDateTime,endDateTime, false, null, null , null);
        if (Objects.nonNull(listHospitalAll) && listHospitalAll.size()>0){
            Document hospitalAll = listHospitalAll.get(0);
            listWashMonitorVo.setHospital(init("全院合规率",hospitalAll.getDouble("allViolationRatio"),hospitalAll.getDouble("allNoWashRatio"),hospitalAll.getDouble("allNoAlarmRatio")));
        }
        //所有科室事件
        List<Document> listDepartmentAll = eventDao.eventCount(startDateTime,endDateTime,true,null, null , null);
        List<ListWashMonitorVo.Ratio> list = new ArrayList<>();
        listDepartmentAll.forEach(document -> {
            list.add(init(document.getString("_id"),document.getDouble("allViolationRatio"),document.getDouble("allNoWashRatio"),document.getDouble("allNoAlarmRatio")));
        });
        listWashMonitorVo.setDepartment(list);
        return listWashMonitorVo;
    }

    @Override
    public IPageResultData<List<ListUserWashMonitorVo>> userWashRatio(UserType userType, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        List<Document> list = eventDao.eventCount(startDateTime,endDateTime,null,userType, null , lionPage);
        List<ListUserWashMonitorVo> returnList = new ArrayList<>();
        list.forEach(document -> {
            ListUserWashMonitorVo vo = new ListUserWashMonitorVo();
            vo.setViolation(new BigDecimal(document.getDouble("allViolationRatio")).setScale(2, BigDecimal.ROUND_HALF_UP));
            vo.setNoWash(new BigDecimal(document.getDouble("allNoWashRatio")).setScale(2, BigDecimal.ROUND_HALF_UP));
            vo.setConformance(new BigDecimal(document.getDouble("allNoAlarmRatio")).setScale(2, BigDecimal.ROUND_HALF_UP));
            vo.setUserId(document.getLong("_id"));
            User user = userExposeService.findById(vo.getUserId());
            if (Objects.nonNull(user)){
                vo.setUserName(user.getName());
                vo.setHeadPortrait(user.getHeadPortrait());
                vo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                Department department = departmentUserExposeService.findDepartment(vo.getUserId());
                if (Objects.nonNull(department)){
                    vo.setDepartmentName(department.getName());
                }
            }
            returnList.add(vo);
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
        List<Document> userWash = eventDao.eventCount(startDateTime,endDateTime,null,null, userId , null);
        if (Objects.nonNull(userWash) && userWash.size()>0) {
            vo.setConformance(new BigDecimal(userWash.get(0).getDouble("allNoAlarmRatio")).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        List<Event> list = eventDao.userWashDetails(userId,startDateTime,endDateTime,lionPage);
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

    private ListWashMonitorVo.Ratio init(String name, Double violation, Double noWash, Double conformance) {
        ListWashMonitorVo.Ratio ratio = new ListWashMonitorVo.Ratio();
        ratio.setName(name);
        ratio.setViolation(new BigDecimal(violation).setScale(2, BigDecimal.ROUND_HALF_UP));
        ratio.setNoWash(new BigDecimal(noWash).setScale(2, BigDecimal.ROUND_HALF_UP));
        ratio.setConformance(new BigDecimal(conformance).setScale(2, BigDecimal.ROUND_HALF_UP));
        return ratio;
    }

}
