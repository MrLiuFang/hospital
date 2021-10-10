package com.lion.event.entity.vo;

import com.lion.device.entity.enums.TagPurpose;
import com.lion.event.entity.Position;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/9 上午9:41
 */
@Data
@Schema
public class ListPositionVo extends Position {

    @Schema(description = "标签类型")
    private TagPurpose tagPurpose;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "标签编码")
    private String tagCode;

    @Schema(description = "科室名称（详情显示）")
    private String departmentName;


}
