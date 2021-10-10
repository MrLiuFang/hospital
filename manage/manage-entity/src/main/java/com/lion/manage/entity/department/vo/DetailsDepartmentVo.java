package com.lion.manage.entity.department.vo;

import com.lion.manage.entity.department.Department;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午10:23
 */
@Data
@Schema
public class DetailsDepartmentVo extends Department {

    @Schema(description = "负责人")
    private List<ResponsibleUserVo> responsibleUser;

    @Schema(description = "负责人ID（原始值）")
    private List<Long> responsible;
}
