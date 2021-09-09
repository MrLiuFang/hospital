package com.lion.manage.service.rule;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.rule.AlarmWay;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/18 上午9:52
 **/
public interface AlarmWayService extends BaseService<AlarmWay> {

    /**
     * 新增警报方式
     * @param alarmId
     * @param ways
     */
    public void add(Long alarmId, List<com.lion.manage.entity.enums.AlarmWay> ways);

    /**
     * 查询
     * @param alarmId
     * @return
     */
    public List<com.lion.manage.entity.enums.AlarmWay> find(Long alarmId);
}
