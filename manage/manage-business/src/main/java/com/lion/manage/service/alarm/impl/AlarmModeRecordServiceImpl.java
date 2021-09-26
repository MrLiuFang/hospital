package com.lion.manage.service.alarm.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.alarm.AlarmModeRecordDao;
import com.lion.manage.entity.alarm.AlarmModeRecord;
import com.lion.manage.service.alarm.AlarmModeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/26 下午8:08
 */
@Service
public class AlarmModeRecordServiceImpl extends BaseServiceImpl<AlarmModeRecord> implements AlarmModeRecordService {

    @Autowired
    private AlarmModeRecordDao alarmModeRecordDao;
}
