package com.lion.event.entity.vo;

import com.lion.common.enums.Type;
import com.lion.event.entity.HumitureRecord;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/9 下午4:54
 */
@Data
@Schema
public class ListHumitureRecordVo extends HumitureRecord {

    @Schema(description = "温度/湿度仪")
    private Type type;

    @Schema(description = "设备编码")
    private String deviceCode;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "标签编码")
    private String tagCode;


}
