package com.lion.event.entity.vo;

import com.lion.common.enums.Type;
import com.lion.event.entity.TagRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/9 下午4:54
 */
@Data
@ApiModel
public class ListTagRecordVo extends TagRecord {

    @ApiModelProperty(value = "温度/湿度仪")
    private Type type;

    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "标签编码")
    private String tagCode;


}
