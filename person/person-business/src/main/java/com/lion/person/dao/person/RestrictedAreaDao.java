package com.lion.person.dao.person;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.entity.person.RestrictedArea;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:05
 */
public interface RestrictedAreaDao extends BaseDao<RestrictedArea> {

    /**
     * 根据人员id删除
     * @param personId
     * @return
     */
    public int deleteByPersonId(Long personId);

    /**
     * 查询人员限制区域
     * @param personId
     * @return
     */
    public List<RestrictedArea> findByPersonId(Long personId);
}
