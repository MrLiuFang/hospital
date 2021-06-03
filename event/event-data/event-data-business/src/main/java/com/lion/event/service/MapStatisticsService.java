package com.lion.event.service;

import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.vo.*;

import javax.validation.constraints.NotNull;
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
    public List<DepartmentStatisticsDetailsVo> departmentStatisticsDetails();

    /**
     * 科室员工统计
     * @return
     * @param name
     */
    public DepartmentStaffStatisticsDetailsVo departmentStaffStatisticsDetails(String name);

    /**
     * 科室资产统计
     * @return
     * @param keyword
     */
    public DepartmentAssetsStatisticsDetailsVo departmentAssetsStatisticsDetails(String keyword);

    /**
     * 科室标签统计
     * @param keyword
     * @return
     */
    public DepartmentTagStatisticsDetailsVo departmentTagStatisticsDetails(String keyword);

    /**
     *
     * @param name
     * @return
     */
    public DepartmentPatientStatisticsDetailsVo departmentPatientStatisticsDetails(String name);

    /**
     * 员工详情
     * @param userId
     * @return
     */
    public StaffDetailsVo staffDetails(Long userId);

    /**
     * 获取员工当前位置
     * @param userId
     * @return
     */
    public UserCurrentRegionVo userCurrentRegion(Long userId);

    /**
     * 获取资产详情
     * @param assetsId
     * @return
     */
    public AssetsDetailsVo assetsDetails(Long assetsId);

    /**
     * 获取警告列表
     * @param lionPage
     * @return
     */
    public IPageResultData<List<SystemAlarmVo>> systemAlarmList(LionPage lionPage);

}
