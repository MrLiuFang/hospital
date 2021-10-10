package com.lion.manage.entity.department.vo;

import com.lion.manage.entity.department.Department;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午8:24
 */
@Data
@Schema
public class ListDepartmentVo extends Department {

    @Schema(description = "负责人")
    private List<ResponsibleUserVo> responsibleUser;
}
