package com.lion.device.entity.device.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.device.Device;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:53
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true,value = {"lastDataTime","warrantyPeriodDate","electricity","createDateTime","updateDateTime","createUserId","updateUserId"})
public class UpdateDeviceDto  extends Device {
}
