package com.lion.manage.expose.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.AlarmDao;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.rule.Alarm;
import com.lion.manage.expose.rule.AlarmExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/27 下午8:17
 **/
@DubboService(interfaceClass = AlarmExposeService.class)
public class AlarmExposeServiceImpl extends BaseServiceImpl<Alarm> implements AlarmExposeService {

    @Autowired
    private AlarmDao alarmDao;

    @Override
    public Alarm find(AlarmClassify classify, SystemAlarmType code) {
        return alarmDao.findFirstByClassifyAndCode(classify,code);
    }
}
