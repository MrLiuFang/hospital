package com.lion.event.dao;

import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.SystemAlarmReport;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/28 下午1:57
 */
public interface SystemAlarmReportDao extends MongoRepository<SystemAlarmReport,String> {
}
