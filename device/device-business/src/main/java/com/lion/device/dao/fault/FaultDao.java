package com.lion.device.dao.fault;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.fault.Fault;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 上午8:45
 */
public interface FaultDao extends BaseDao<Fault> {

    public Fault findFirstByRelationIdOrderByCreateDateTimeDesc(Long relationId);
}
