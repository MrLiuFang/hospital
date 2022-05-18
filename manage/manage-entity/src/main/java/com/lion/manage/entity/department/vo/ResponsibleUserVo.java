package com.lion.manage.entity.department.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午8:26
 */
@Data
@Schema
public class ResponsibleUserVo {

    @Schema(description = "负责人id")
    private Long id;

    @Schema(description = "负责人姓名")
    private String name;

    @Schema(description = "负责人头像地址")
    private String headPortraitUrl;


    @Schema(description = "负责人头像Id")
    private Long headPortrait;
}
