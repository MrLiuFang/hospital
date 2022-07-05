package com.lion.manage.expose.region;

import com.lion.core.LionObjectMapper;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.region.RegionWarningBell;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/25 下午7:54
 */
public interface RegionWarningBellExposeService extends BaseService<RegionWarningBell> {

    /**
     * 根据警示铃查询关联关系
     * @param warningBellId
     * @return
     */
    public RegionWarningBell find(Long warningBellId);

    public List<Long> findAllBindId();
}
