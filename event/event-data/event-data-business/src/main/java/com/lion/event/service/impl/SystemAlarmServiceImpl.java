package com.lion.event.service.impl;

import com.lion.event.dao.SystemAlarmDao;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.vo.RegionStatisticsDetails;
import com.lion.event.service.SystemAlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:07
 **/
@Service
public class SystemAlarmServiceImpl implements SystemAlarmService {

    @Autowired
    private SystemAlarmDao alarmDao;

    @Override
    public void save(SystemAlarm systemAlarm) {
        alarmDao.save(systemAlarm);
    }

    @Override
    public void updateSdt(String uuid) {
        alarmDao.updateSdt(uuid);
    }

    @Override
    public void unalarm(String uuid) {
        alarmDao.unalarm(uuid);
    }

    @Override
    public Map<Long, RegionStatisticsDetails> groupCount(Long buildFloorId, Map<Long, RegionStatisticsDetails> map) {
        return alarmDao.groupCount(buildFloorId, map);
    }

    @Override
    public Map<String, Integer> groupCount(Long departmentId) {
        return alarmDao.groupCount(departmentId);
    }

    @Override
    public List<SystemAlarm> find(Long userId, Boolean ua, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return alarmDao.find(userId, ua, startDateTime, endDateTime);
    }
}
