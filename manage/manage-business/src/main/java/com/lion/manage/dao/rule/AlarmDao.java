package com.lion.manage.dao.rule;

import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.rule.Alarm;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/13下午1:36
 */
public interface AlarmDao extends BaseDao<Alarm> {

    /**
     * 根据警报分类查询
     * @param classify
     * @return
     */
    public Alarm findFirstByClassify(AlarmClassify classify);

    /**
     * 根据警报分类查询
     * @param classify
     * @param content
     * @return
     */
    public Alarm findFirstByClassifyAndContent(AlarmClassify classify,String content);

    /**
     * 根据警报分类和级别查询
     * @param classify
     * @param level
     * @return
     */
    public Alarm findFirstByClassifyAndLevel(AlarmClassify classify,Integer level);

    /**
     * 根据警报分类和级别查询
     * @param classify
     * @param level
     * @param content
     * @return
     */
    public Alarm findFirstByClassifyAndLevelAndContent(AlarmClassify classify,Integer level,String content);

    /**
     * 根据编码查询
     * @param code
     * @param classify
     * @return
     */
    public Alarm findFirstByCodeAndClassify(SystemAlarmType code,AlarmClassify classify);
}
