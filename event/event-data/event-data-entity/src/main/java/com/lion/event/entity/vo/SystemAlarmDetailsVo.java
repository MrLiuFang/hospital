package com.lion.event.entity.vo;

import com.lion.common.enums.Type;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.event.entity.SystemAlarm;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/4 上午9:24
 */
@Data
@Schema
public class SystemAlarmDetailsVo extends SystemAlarm {

    @Schema(description = "警告来源(com.lion.common.enums.Type 获取该字典)")
    private Type type;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "图片")
    private Long img;

    @Schema(description = "图片url")
    private String imgUrl;

//    @Schema(description = "患者行动限制区域")
//    private List<String> restrictedArea;

    @Schema(description = "警告内容")
    private String alarmContent;

    @Schema(description = "警告内容编码(com.lion.manage.entity.enums.SystemAlarmType 获取该字典)")
    private String alarmCode;

    @Schema(description = "员工姓名")
    private String userName;

    @Schema(description = "员工编码")
    private Integer userNumber;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "流动人员姓名")
    private String temporaryPersonName;

    @Schema(description = "标签编码")
    private String tagCode;

    @Schema(description = "标签分类")
    private TagType tagType;

    @Schema(description = "标签用途")
    private TagPurpose tagPurpose;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "设备编号")
    private String deviceCode;

    @Schema(description = "资产名称")
    private String assetsName;

    @Schema(description = "资产编号")
    private String assetsCode;

    @Schema(description = "blueCode")
    private String blueCode;

    @Schema(description = "处理人头像")
    private Long uuHeadPortrait;

    @Schema(description = "处理人头像Url")
    private String uuHeadPortraitUrl;

    @Schema(description = "电量")
    private Integer battery;

    @Schema(description = "汇报")
    private List<SystemAlarmReportDetailsVo> systemAlarmReportDetailsVos;


}
