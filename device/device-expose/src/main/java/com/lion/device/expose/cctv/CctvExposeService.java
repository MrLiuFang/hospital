package com.lion.device.expose.cctv;

import com.lion.core.service.BaseService;
import com.lion.device.entity.cctv.Cctv;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午9:33
 */
public interface CctvExposeService extends BaseService<Cctv> {

    /**
     * 根据id数组查询
     * @param ids
     * @return
     */
    public List<Cctv> find(List<Long> ids);

    /**
     * 根据编码查询
     * @param code
     * @return
     */
    public Cctv find(String code);

    /**
     * 关联cctv所在的位置
     * @param oldCctvIds 用于清除已经关联位置(清空buildId,buildFloorId,regionId)
     * @param newCctvIds
     * @param buildId
     * @param buildFloorId
     * @param regionId
     * @param departmentId
     */
    public void relationPosition(List<Long> oldCctvIds,List<Long> newCctvIds,Long buildId,Long buildFloorId,Long regionId,Long departmentId);

    /**
     * 根据科室统计cctv数量
     * @param departmentId
     * @return
     */
    public Integer count(Long departmentId);
}
