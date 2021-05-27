package com.lion.device.dao.device;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.device.entity.device.vo.ListDeviceMonitorVo;
import com.lion.device.entity.enums.DeviceMonitorState;
import org.springframework.data.domain.Page;

import java.util.List;

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
     * @param state
     * @param lionPage
     * @return
     */
    Page deviceMonitorList(Long buildId, Long buildFloorId, DeviceMonitorState state, LionPage lionPage);
}
