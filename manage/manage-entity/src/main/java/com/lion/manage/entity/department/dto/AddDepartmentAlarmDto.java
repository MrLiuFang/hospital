package com.lion.manage.entity.department.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.department.DepartmentAlarm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/27 下午4:09
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"id","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddDepartmentAlarmDto extends DepartmentAlarm {

}
