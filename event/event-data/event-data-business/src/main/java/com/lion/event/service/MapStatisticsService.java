package com.lion.event.service;

import com.lion.event.entity.vo.DepartmentAssetsStatisticsDetails;
import com.lion.event.entity.vo.DepartmentStaffStatisticsDetails;
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
     * @param name
     */
    public List<DepartmentStatisticsDetails> departmentStatisticsDetails();

    /**
     * 科室员工统计
     * @return
     * @param name
     */
    public List<DepartmentStaffStatisticsDetails>  departmentStaffStatisticsDetails(String name);

    /**
     * 科室资产统计
     * @return
     * @param keyword
     */
    public List<DepartmentAssetsStatisticsDetails>  departmentAssetsStatisticsDetails(String keyword);

}
