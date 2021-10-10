package com.lion.device.entity.device.vo;

import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.DeviceGroup;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午8:29
 */
@Data
@Schema
public class ListDeviceGroupVo extends DeviceGroup {

    @Schema(description = "设备数量")
    private Integer deviceQuantity;

    @Schema(description = "设备组关联的所有设备(某些功能模块能用到)")
    private List<Device> devices;
}
