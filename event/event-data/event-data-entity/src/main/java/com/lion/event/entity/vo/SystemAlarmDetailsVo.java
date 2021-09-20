package com.lion.event.entity.vo;

import com.lion.common.enums.Type;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.event.entity.SystemAlarm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel
public class SystemAlarmDetailsVo extends SystemAlarm {

    @ApiModelProperty(value = "警告来源(com.lion.common.enums.Type 获取该字典)")
    private Type type;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "图片")
    private Long img;

    @ApiModelProperty(value = "图片url")
    private String imgUrl;

//    @ApiModelProperty(value = "患者行动限制区域")
//    private List<String> restrictedArea;

    @ApiModelProperty(value = "警告内容")
    private String alarmContent;

    @ApiModelProperty(value = "警告内容编码(com.lion.manage.entity.enums.SystemAlarmType 获取该字典)")
    private String alarmCode;

    @ApiModelProperty(value = "员工姓名")
    private String userName;

    @ApiModelProperty(value = "员工编码")
    private Integer userNumber;

    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    @ApiModelProperty(value = "流动人员姓名")
    private String temporaryPersonName;

    @ApiModelProperty(value = "标签编码")
    private String tagCode;

    @ApiModelProperty(value = "标签分类")
    private TagType tagType;

    @ApiModelProperty(value = "标签用途")
    private TagPurpose tagPurpose;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    @ApiModelProperty(value = "资产名称")
    private String assetsName;

    @ApiModelProperty(value = "资产编号")
    private String assetsCode;

    @ApiModelProperty(value = "blueCode")
    private String blueCode;

    @ApiModelProperty(value = "处理人头像")
    private Long uuHeadPortrait;

    @ApiModelProperty(value = "处理人头像Url")
    private String uuHeadPortraitUrl;

    @ApiModelProperty(value = "电量")
    private Integer battery;

    @ApiModelProperty(value = "汇报")
    private List<SystemAlarmReportDetailsVo> systemAlarmReportDetailsVos;


}
