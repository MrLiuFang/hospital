package com.lion.manage.service.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.AlarmWayDao;
import com.lion.manage.entity.rule.AlarmWay;
import com.lion.manage.service.rule.AlarmWayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/18 上午9:52
 **/
@Service
public class AlarmWayServiceImpl extends BaseServiceImpl<AlarmWay> implements AlarmWayService {

    @Autowired
    private AlarmWayDao alarmWayDao;

    @Override
    @Transactional
    public void add(Long alarmId, List<com.lion.manage.entity.enums.AlarmWay> ways) {
        if (Objects.nonNull(alarmId)){
            alarmWayDao.deleteByAlarmId(alarmId);
        }
        if (Objects.nonNull(ways) && ways.size()>0) {
            ways.forEach(alarmWay -> {
                AlarmWay a = new AlarmWay();
                a.setAlarmId(alarmId);
                a.setWay(alarmWay);
                save(a);
            });
        }
    }

    @Override
    public List<com.lion.manage.entity.enums.AlarmWay> find(Long alarmId) {
        List<AlarmWay> list = alarmWayDao.findByAlarmId(alarmId);
        List<com.lion.manage.entity.enums.AlarmWay> returnList = new ArrayList<>();
        list.forEach(alarmWay -> {
            returnList.add(alarmWay.getWay());
        });
        return returnList;
    }
}
