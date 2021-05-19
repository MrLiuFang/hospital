package com.lion.manage.expose.rule;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.rule.WashDeviceType;

import java.util.List;

public interface WashDeviceTypeExposeService extends BaseService<WashDeviceType> {

    /**
     * 根据洗手规则id查询洗手设备类型
     * @param washId
     * @return
     */
    public List<com.lion.manage.entity.enums.WashDeviceType> find(Long washId);
}
