package com.lion.device.dao.device;

import com.lion.core.LionPage;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.enums.State;
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
     *
     * @param buildId
     * @param buildFloorId
     * @param deviceClassify
     * @param deviceType
     * @param deviceState
     * @param name
     * @param lionPage
     * @return
     */
    Page deviceMonitorList(Long buildId, Long buildFloorId, DeviceClassify deviceClassify, DeviceType deviceType, State deviceState, String name, LionPage lionPage);

    /**
     * 设备状态
     * @param lionPage
     * @return
     */
    Page deviceState(LionPage lionPage);

    /**
     * 查寻设备
     * @param regionIds
     * @param name
     * @param code
     * @return
     */
    public List<Device> find(List<Long> regionIds, String name, String code);
}
