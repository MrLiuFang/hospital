package com.lion.manage.dao.rule;

import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.rule.Alarm;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

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
     * @param code
     * @return
     */
    public Alarm findFirstByClassifyAndCode(AlarmClassify classify,SystemAlarmType code);


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

    /**
     *
     * @param code
     * @param classify
     * @param level
     * @return
     */
    public Alarm findFirstByCodeAndClassifyAndLevel(SystemAlarmType code,AlarmClassify classify,Integer level);

    @Query("select a from Alarm a join AlarmUser au on a.id = au.alarmId where au.userId = :userId")
    public List<Alarm> findByUserId(@Param("userId") Long userId);
}
