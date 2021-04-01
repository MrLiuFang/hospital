package com.lion.manage.service.region;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.region.RegionCctv;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:10
 */
public interface RegionCctvService extends BaseService<RegionCctv> {

    /**
     * 保存区域与cctv的关联关系
     * @param regionId
     * @param cctvIds
     * @return
     */
    public void save(Long regionId, List<Long> cctvIds);

    /**
     * 根据区域id查询所有关联的cctv
     * @param regionId
     * @return
     */
    public List<RegionCctv> find(Long regionId);
}
