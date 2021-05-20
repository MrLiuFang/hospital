package com.lion.event.dao;

import com.lion.common.enums.Type;
import com.lion.event.entity.vo.RegionStatisticsDetails;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/20 上午11:03
 */
public interface CurrentPositionDaoEx {

    /**
     * 根据区域统计区域的员工，患者，标签……数量
     * @param buildFloorId
     * @param map
     * @return
     */
    public Map<Long, RegionStatisticsDetails> groupCount(Long buildFloorId,Map<Long, RegionStatisticsDetails> map);
}
