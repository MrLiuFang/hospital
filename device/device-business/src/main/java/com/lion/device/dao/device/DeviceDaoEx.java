package com.lion.device.dao.device;

import com.lion.core.LionPage;
import com.lion.device.entity.enums.State;
import org.springframework.data.domain.Page;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/27 下午2:28
 */
public interface DeviceDaoEx {

    /**
     * 设备监控列表
     * @param buildId
     * @param buildFloorId
     * @param deviceSate
     * @param lionPage
     * @return
     */
    Page deviceMonitorList(Long buildId, Long buildFloorId, State deviceSate, LionPage lionPage);
}
