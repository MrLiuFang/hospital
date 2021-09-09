package com.lion.manage.dao.rule;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.rule.Alarm;
import com.lion.manage.entity.rule.AlarmWay;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/18 上午9:50
 **/
public interface AlarmWayDao extends BaseDao<AlarmWay> {

    /**
     * 根就警报ID查询警报方式
     * @param alarmId
     * @return
     */
    public List<AlarmWay> findByAlarmId(Long alarmId);

    /**
     * 根据警报id删除
     * @param alarmId
     * @return
     */
    public int deleteByAlarmId(Long alarmId);
}
