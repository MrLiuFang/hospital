package com.lion.manage.service.region;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.region.Region;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:07
 */
public interface RegionService extends BaseService<Region> {

    /**
     * 根据科室查询区域
     * @param departmentId
     * @return
     */
    public List<Region> find(Long departmentId);
}
