package com.lion.device.entity.device.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.device.DeviceGroup;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午8:22
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
public class UpdateDeviceGroupDto extends DeviceGroup {

    @Schema(description = "设备组关联的设备ID")
    private List<Long> deviceIds;
}
