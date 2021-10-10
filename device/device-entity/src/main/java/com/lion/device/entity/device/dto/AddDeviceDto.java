package com.lion.device.entity.device.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.device.Device;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:51
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"id","lastDataTime","warrantyPeriodDate","electricity","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddDeviceDto extends Device {

}
