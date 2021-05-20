package com.lion.manage.expose.assets;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.Assets;

import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/16 上午9:49
 **/
public interface AssetsExposeService extends BaseService<Assets> {

    /**
     * 根据标签查村资产
     * @param tagId
     * @return
     */
    public Assets find(Long tagId);

    /**
     * 根据楼层统计区域内的资产数量
     * @param buildFloorId
     * @return
     */
    public List<Map<String, Object>> count(Long buildFloorId);

    /**
     * 统计科室内的资产数量
     * @param departmentId
     * @return
     */
    public Integer countByDepartmentId(Long departmentId);
}
