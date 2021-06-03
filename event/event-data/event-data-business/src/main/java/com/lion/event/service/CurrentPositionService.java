package com.lion.event.service;

import com.lion.event.entity.CurrentPosition;
import com.lion.event.entity.Position;
import com.lion.event.entity.vo.RegionStatisticsDetails;

import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/15 下午3:01
 **/
public interface CurrentPositionService  {

    /**
     * 保存当前位置
     * @param position
     */
    public void save(Position position);

    /**
     * 查询用户当前位置
     * @param pi
     * @return
     */
    public CurrentPosition find(Long pi);

    /**
     * 获取标签当前位置
     * @param tagId
     * @return
     */
    public CurrentPosition findByTagId(Long tagId);

    /**
     * 根据区域统计区域的员工，患者，标签……数量
     * @param buildFloorId
     * @param map
     * @return
     */
    public Map<Long, RegionStatisticsDetails> groupCount(Long buildFloorId,Map<Long, RegionStatisticsDetails> map);

}
