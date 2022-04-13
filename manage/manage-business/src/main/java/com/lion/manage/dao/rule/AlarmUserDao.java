package com.lion.manage.dao.rule;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.rule.AlarmUser;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mr.Liu
 * @classname AlarmUserDao
 * @description
 * @date 2022/04/10 上午10:48
 */
public interface AlarmUserDao extends BaseDao<AlarmUser> {

    @Transactional
    public int deleteByAlarmId(Long alarmId);
}
