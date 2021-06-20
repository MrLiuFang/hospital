package com.lion.event.service;

import com.lion.common.dto.DeviceDataDto;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.manage.entity.assets.Assets;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.upms.entity.user.User;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/20 上午10:28
 */
public interface BatteryAlarmService {

    /**
     * 设备低电量警告
     * @param device
     */
    public void deviceLowBatteryAlarm(Device device, DeviceDataDto deviceDataDto);

    /**
     * 资产低电量警告
     * @param assets
     * @param deviceDataDto
     * @param tag
     */
    public void assetsLowBatteryAlarm(Assets assets, DeviceDataDto deviceDataDto, Tag tag);

    /**
     * 员工低电量警告
     * @param user
     * @param deviceDataDto
     * @param tag
     */
    public void userLowBatteryAlarm(User user, DeviceDataDto deviceDataDto, Tag tag);

    /**
     * 病人低电量警告
     * @param patient
     * @param deviceDataDto
     * @param tag
     */
    public void patientLowBatteryAlarm(Patient patient,DeviceDataDto deviceDataDto, Tag tag);

    /**
     * 流动人员低电压警告
     * @param temporaryPerson
     * @param deviceDataDto
     * @param tag
     */
    public void temporaryPersonLowBatteryAlarm(TemporaryPerson temporaryPerson, DeviceDataDto deviceDataDto, Tag tag);

    /**
     * 标签低电量警告（温湿标签）
     * @param deviceDataDto
     * @param tag
     */
    public void tagLowBatteryAlarm(DeviceDataDto deviceDataDto, Tag tag);

}
