package com.lion.device.entity.tag.vo;

import com.lion.device.entity.tag.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午9:18
 **/
@Data
@Schema
public class ListTagVo extends Tag {

    @Schema(description = "绑定Id")
    private Long bindingId;

    @Schema(description = "绑定对象")
    private String bindingName;

    @Schema(description = "科室")
    private String departmentName;
}
