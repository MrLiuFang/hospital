package com.lion.manage.service.rule;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.enums.WashDeviceType;
import com.lion.manage.entity.rule.WashDevice;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午4:59
 */
public interface WashDeviceService extends BaseService<WashDevice> {

    /**
     * 保存
     * @param deviceId
     * @param washId
     */
    public void add(List<Long> deviceId, Long washId);

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
    public List<WashDevice> find(Long washId);
}
