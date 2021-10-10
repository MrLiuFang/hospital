package com.lion.device.entity.device.vo;

import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.DeviceGroup;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午8:49
 */
@Data
@Schema
public class DetailsDeviceGroupVo extends DeviceGroup {

    @Schema(description = "设备")
    private List<DeviceVo> devices;

    @Data
    @Schema
    public static class DeviceVo extends Device {
        @Schema(description = "设备组id")
        private Long deviceGroupId;
    }
}
