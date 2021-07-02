package com.lion.device.dao.fault;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.enums.FaultType;
import com.lion.device.entity.fault.Fault;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 上午8:45
 */
public interface FaultDao extends BaseDao<Fault> {

    /**
     * 查询设备最后一条故障
     * @param relationId
     * @return
     */
    public Fault findFirstByRelationIdOrderByCreateDateTimeDesc(Long relationId);

    /**
     * 统计故障
     * @param isSolve
     * @param type
     * @return
     */
    public int countByIsSolveAndType(Boolean isSolve,FaultType type);

    /**
     * 统计未处理的故障
     * @param relationId
     * @return
     */
    public int countByRegionIdAndIsSolveIsFalse(Long relationId);
}
