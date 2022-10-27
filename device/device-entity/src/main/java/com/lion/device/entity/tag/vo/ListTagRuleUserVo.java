package com.lion.device.entity.tag.vo;

import com.lion.core.persistence.Validator;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午11:47
 **/
@Data
@Schema
public class ListTagRuleUserVo {

    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "用户姓名")
    private String name;

    @Schema(description = "用户职位")
    private String position;

    @Schema(description = "员工编号")
    private Integer number;

    @Schema(description = "标签编码")
    private String tagCode;

    @Schema(description = "头像（文件id）")
    private Long headPortrait;

    @Schema(description = "头像")
    private String headPortraitUrl;

    @Schema(description = "科室名称")
    private String departmentName;
}
