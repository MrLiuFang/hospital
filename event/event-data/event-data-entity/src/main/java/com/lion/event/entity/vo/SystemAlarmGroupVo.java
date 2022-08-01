package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.enums.TagType;
import com.lion.manage.entity.assets.AssetsType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @classname SystemAlarmGroupVo
 * @description
 * @date 2022/04/13 下午5:15
 */
@Data
public class SystemAlarmGroupVo {

    @Schema(description = "标签Id")
    private Long tagId;

    @Schema(description = "标签码")
    private String tagCode;

    @Schema(description = "标签类型")
    private TagType tagType;

    @Schema(description = "资产Id")
    private Long assetsId;

    @Schema(description = "资产码")
    private String assetsCode;

    @Schema(description = "资产类型")
    private AssetsType assetsType;

    @Schema(description = "设备Id")
    private Long deviceId;

    @Schema(description = "设备码")
    private String deviceCode;

    @Schema(description = "设备类型")
    private DeviceType deviceType;


    @Schema(description = "警告数量")
    private Integer count;

    @Schema(description = "资产/设备/警告人姓名/资产名称/tag名称.......")
    private String title;

    @Schema(description = "图片ID")
    private Long imgId;

    @Schema(description = "图片url")
    private String imgUrl;

    @Schema(description = "告警内容")
    private SystemAlarmVo systemAlarm;

    @Schema(description = "告警时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    @Schema(description = "是否标签，患者，流动人员，温湿……")
    private Boolean isTag;

    @Schema(description = "是否设备……")
    private Boolean isDevice;

    @Schema(description = "是否资产……")
    private Boolean isAssets;

}
