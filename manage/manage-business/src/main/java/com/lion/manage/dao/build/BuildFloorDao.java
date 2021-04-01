package com.lion.manage.dao.build;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:52
 */
public interface BuildFloorDao extends BaseDao<BuildFloor> {

    /**
     * 根据建筑删除
     * @param buildId
     * @return
     */
    public int deleteByBuildId(Long buildId);

    /**
     * 根据建筑查询
     * @param buildId
     * @return
     */
    public List<BuildFloor> findByBuildId(Long buildId);

    /**
     * 根据建筑和楼层查询
     * @param buildId
     * @param name
     * @return
     */
    public BuildFloor findFirstByBuildIdAndName(Long buildId,String name);
}
