package com.lion.event.entity.vo;

import com.lion.device.entity.enums.TagPurpose;
import com.lion.event.entity.Position;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/9 上午9:41
 */
@Data
@ApiModel
public class ListPositionVo extends Position {

    @ApiModelProperty(value = "标签类型")
    private TagPurpose tagPurpose;

    @ApiModelProperty(value = "标签名称/设备名称")
    private String deviceName;

    @ApiModelProperty(value = "标签编码")
    private String tagCode;

    @ApiModelProperty(value = "科室名称（详情显示）")
    private String departmentName;


}
