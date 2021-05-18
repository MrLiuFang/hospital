package com.lion.manage.expose.rule;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.rule.Alarm;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/27 下午8:16
 **/
public interface AlarmExposeService extends BaseService<Alarm> {

    public Alarm find(AlarmClassify classify, SystemAlarmType systemAlarmType);
}
