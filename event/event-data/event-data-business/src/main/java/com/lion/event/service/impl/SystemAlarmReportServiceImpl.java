package com.lion.event.service.impl;

import com.lion.common.enums.SystemAlarmState;
import com.lion.common.expose.file.FileExposeService;
import com.lion.event.dao.SystemAlarmReportDao;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.SystemAlarmReport;
import com.lion.event.entity.dto.AlarmReportDto;
import com.lion.event.entity.vo.SystemAlarmReportDetailsVo;
import com.lion.event.service.SystemAlarmReportService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
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
 * @time: 2021/6/28 下午1:57
 */
@Service
public class SystemAlarmReportServiceImpl implements SystemAlarmReportService {

    @Autowired
    private SystemAlarmReportDao systemAlarmReportDao;

    @DubboReference
    private UserExposeService userExposeService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DubboReference
    private FileExposeService fileExposeService;


    @Override
    public void alarmReport(AlarmReportDto alarmReportDto) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        if (Objects.nonNull(userId)) {
            User user = userExposeService.findById(userId);
            if (Objects.nonNull(user)){
                SystemAlarmReport systemAlarmReport = new SystemAlarmReport();
                systemAlarmReport.setSli(alarmReportDto.getId());
                systemAlarmReport.setRdt(LocalDateTime.now());
                systemAlarmReport.setRe(alarmReportDto.getReport());
                systemAlarmReport.setRui(user.getId());
                systemAlarmReport.setRun(user.getName());
                systemAlarmReport.setRnu(user.getNumber());
                systemAlarmReportDao.save(systemAlarmReport);
            }
        }
    }

    @Override
    public List<SystemAlarmReportDetailsVo> list(String systemAlarmId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (StringUtils.hasText(systemAlarmId)) {
            criteria.and("sli").is(systemAlarmId);
        }
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Direction.DESC,"rdt"));
        List<SystemAlarmReport> items = mongoTemplate.find(query,SystemAlarmReport.class);
        List<SystemAlarmReportDetailsVo> returnList = new ArrayList<>();
        items.forEach(systemAlarmReport -> {
            SystemAlarmReportDetailsVo vo = new SystemAlarmReportDetailsVo();
            BeanUtils.copyProperties(systemAlarmReport,vo);
            User user = userExposeService.findById(systemAlarmReport.getRui());
            if (Objects.nonNull(user)){
                vo.setHeadPortrait(user.getHeadPortrait());
                vo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            returnList.add(vo);
        });
        return returnList;
    }
}
