package com.lion.device.expose.device;

import com.lion.core.service.BaseService;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.vo.DetailsDeviceVo;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.State;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午8:22
 */
public interface DeviceExposeService extends BaseService<Device> {

    /**
     * 根据设备组查询所有设备
     * @param deviceGroupId
     * @return
     */
    public List<Device> findByDeviceGruopId(Long deviceGroupId);

    /**
     * 根据编码查询设备
     * @param code
     * @return
     */
    public Device find(String code);

    /**
     *
     * @param deviceId
     * @param battery
     */
    public void updateBattery(Long deviceId, Integer battery);

    /**
     * 根据设备组统计电量设备
     * @param deviceGroupIds
     * @param battery
     * @return
     */
    public Integer countDevice(List<Long> deviceGroupIds, Integer battery);

    /**
     * 更新设备数据上传时间
     * @param id
     * @param dateTime
     */
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime);

    /**
     * 修改状态
     * @param id
     * @param state
     */
    public void updateState(Long id,Integer state);

    /**
     *
     * @param startPreviousDisinfectDate
     * @param endPreviousDisinfectDate
     * @param name
     * @param code
     * @return
     */
    public List<Device> find(LocalDateTime startPreviousDisinfectDate, LocalDateTime endPreviousDisinfectDate, String name, String code);

    /**
     * 更新最后一次消毒时间
     * @param id
     */
    public void updateDisinfectDate(Long id);

    public List<Long> allId();

    /**
     * 根据区域查询设备
     * @param regionId
     * @return
     */
    public List<Device> findByRegionId(Long regionId);

    /**
     * 根据区域和设备大类查询
     * @param regionId
     * @param deviceClassify
     * @return
     */
    public List<Device> findByRegionIdAndDeviceClassify(Long regionId,DeviceClassify deviceClassify);

    /**
     * 根据科室查询设备
     * @param departmentId
     * @param keyword
     * @return
     */
    public List<Device> findByDepartmentId(Long departmentId,String keyword);

    /**
     * 关联区域
     * @param regionId
     * @param ids
     */
    public void relationRegion(Long regionId,List<Long> ids);

    /**
     * 根据大类统计数量
     * @param classify
     * @param regionId
     * @return
     */
    public int count(DeviceClassify classify, Long regionId);

    /**
     * 根据科室统计
     * @param departmentId
     * @param states
     * @return
     */
    public int count(Long departmentId, List<State> states);

    public long countDeviceClassify(DeviceClassify deviceClassify);

    /**
     * 详情
     * @param id
     * @return
     */
    public DetailsDeviceVo details(Long id);

    public void updateState(String code, State state);

    public void updateRssi(String monitorId, String rssi);



}
