package com.lion.manage.service.region;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.enums.ExposeObject;
import com.lion.manage.entity.region.RegionExposeObject;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:09
 */
public interface RegionExposeObjectService extends BaseService<RegionExposeObject> {

    /**
     * 保存区域公开对象
     * @param regionId
     * @param list
     */
    public void save(Long regionId, List<ExposeObject> list);

    public List<RegionExposeObject> find(Long regionId);
}
