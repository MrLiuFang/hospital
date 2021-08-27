package com.lion.manage.entity.assets.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.core.persistence.Validator;
import com.lion.device.entity.fault.vo.FaultDetailsVo;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.AssetsFault;
import com.lion.manage.entity.enums.SystemAlarmType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import sun.rmi.runtime.Log;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:40
 */
@Data
@ApiModel
public class DetailsAssetsVo extends Assets {

    @ApiModelProperty(value = "位置")
    private String position;

    @ApiModelProperty(value = "故障申报记录-最后一次")
    private DetailsAssetsFaultVo assetsFault;

    @ApiModelProperty(value = "警告")
    private String alarm;

    @ApiModelProperty(value = "警告编码")
    private SystemAlarmType alarmType;

    @ApiModelProperty(value = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @ApiModelProperty(value = "警报ID")
    private String alarmId;

    @ApiModelProperty(value = "借用总次数")
    private Integer borrowCount;

    @ApiModelProperty(value = "故障总次数")
    private Integer faultCount;

    @ApiModelProperty(value = "图片Url")
    private String imgUrl;

    @ApiModelProperty(value = "所属科室名称")
    private String departmentName;

    @ApiModelProperty(value = "所属区域名称")
    private String regionName;

    @ApiModelProperty(value = "所属建筑名称")
    private String buildName;

    @ApiModelProperty(value = "所属建筑楼层名称")
    private String buildFloorName;

    @ApiModelProperty(value = "标签编码")
    private String tagCode;

    @ApiModelProperty(value = "标签Id")
    private Long tagId;
}
