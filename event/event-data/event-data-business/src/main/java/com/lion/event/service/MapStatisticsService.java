package com.lion.event.service;

import com.lion.event.entity.vo.DepartmentStatisticsDetails;
import com.lion.event.entity.vo.RegionStatisticsDetails;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/20 上午9:36
 */
public interface MapStatisticsService {

    /**
     * 区域信息统计（员工，患者，标签，是否有警告）
     * @param buildFloorId
     * @return
     */
    public List<RegionStatisticsDetails> regionStatisticsDetails(Long buildFloorId);


    /**
     * 科室统计
     * @return
     */
    public List<DepartmentStatisticsDetails> departmentStatisticsDetails();
}
