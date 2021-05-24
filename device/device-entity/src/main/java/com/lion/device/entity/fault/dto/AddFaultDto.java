package com.lion.device.entity.fault.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.fault.Fault;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 上午9:18
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true,value = {"relationId","id","departmentName","buildFloorName","buildName","regionName","departmentId","buildFloorId","buildId","regionId","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddFaultDto extends Fault {
}
