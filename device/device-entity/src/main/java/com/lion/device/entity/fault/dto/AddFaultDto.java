package com.lion.device.entity.fault.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.fault.Fault;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 上午9:18
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"relationId","id","departmentName","buildFloorName","buildName","regionName","departmentId","buildFloorId","buildId","regionId","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddFaultDto extends Fault {
}
