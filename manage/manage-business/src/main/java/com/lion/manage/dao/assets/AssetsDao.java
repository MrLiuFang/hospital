package com.lion.manage.dao.assets;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.Assets;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:12
 */
public interface AssetsDao extends BaseDao<Assets> {

    /**
     * 根据名称查询资产
     * @param name
     * @return
     */
    public Assets findFirstByName(String name);

    /**
     * 根据编码查询资产
     * @param code
     * @return
     */
    public Assets findFirstByCode(String code);

    /**
     * 根据楼层统计区域内资产数量
     * @param buildFloorId
     * @return
     */
    @Query( "select new map(regionId as region_id, count(id) as count) from Assets where buildFloorId = :buildFloorId group by regionId " )
    public List<Map<String, Object>> groupRegionCount(Long buildFloorId);

}
