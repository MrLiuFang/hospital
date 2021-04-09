package com.lion.manage.service.rule;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.rule.WashDevice;
import com.lion.manage.entity.rule.WashRegion;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午4:54
 */
public interface WashRegionService extends BaseService<WashRegion> {

    /**
     * 保存
     * @param regionId
     * @param washId
     */
    public void add(List<Long> regionId,Long washId);

    /**
     * 根据洗手规则删除
     * @param washId
     * @return
     */
    public int delete( Long washId);

    /**
     * 根据洗手规则查询
     * @param washId
     * @return
     */
    public List<WashRegion> find(Long washId);
}
