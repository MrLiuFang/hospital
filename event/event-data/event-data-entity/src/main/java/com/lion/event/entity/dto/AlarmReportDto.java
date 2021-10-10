package com.lion.event.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/23 上午10:14
 */
@Data
@Schema
public class AlarmReportDto {

    @Schema(description = "id")
    private String id;

    @Schema(description = "员工id")
    @NotNull(message = "汇报人不能为空")
    private Long userId;

    @Schema(description = "汇报内容（300字符）")
    @Length(max = 300,message = "员工编号不能超过300字符")
    private String report;

}
