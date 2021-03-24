package com.lion.manage.entity.department.vo;

import com.lion.manage.entity.department.Department;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午10:23
 */
@Data
@ApiModel
public class DepartmentDetailsVo extends Department {

    @ApiModelProperty(value = "负责人")
    private List<ResponsibleUserVo> responsibleUser;
}
