package com.lion.manage.dao.rule;

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
     * 根据规则内容和分类查询
     * @param content
     * @param classify
     * @return
     */
    public Alarm findFirstByContentAndClassify(String content, AlarmClassify classify);
}
