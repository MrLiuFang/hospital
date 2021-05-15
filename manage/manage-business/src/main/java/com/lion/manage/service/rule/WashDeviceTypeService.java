package com.lion.manage.service.rule;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.rule.WashDeviceType;

import java.util.List;
import java.util.PrimitiveIterator;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/14 上午10:02
 **/
public interface WashDeviceTypeService extends BaseService<WashDeviceType> {

    /**
     * 新增
     * @param washId
     * @param typeList
     */
    public void add(Long washId, List<com.lion.manage.entity.enums.WashDeviceType> typeList);

    /**
     * 查询洗手设备类型
     * @param washId
     * @return
     */
    public List<com.lion.manage.entity.enums.WashDeviceType> find(Long washId);
}
