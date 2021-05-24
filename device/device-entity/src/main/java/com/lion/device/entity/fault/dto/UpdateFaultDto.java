package com.lion.device.entity.fault.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.fault.Fault;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 上午9:21
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true,value = {"relationId","departmentName","buildFloorName","buildName","regionName","departmentId","buildFloorId","buildId","regionId","createDateTime","updateDateTime","createUserId","updateUserId"})
@Data
public class UpdateFaultDto extends Fault {
}
