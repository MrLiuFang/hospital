package com.lion.manage.entity.assets.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.enums.SystemAlarmType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:40
 */
@Data
@Schema
public class DetailsAssetsVo extends Assets {

    @Schema(description = "位置")
    private String position;

    @Schema(description = "故障申报记录-最后一次")
    private DetailsAssetsFaultVo assetsFault;

    @Schema(description = "警告")
    private String alarm;

    @Schema(description = "警告编码")
    private SystemAlarmType alarmType;

    @Schema(description = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @Schema(description = "警报ID")
    private String alarmId;

    @Schema(description = "借用总次数")
    private Integer borrowCount;

    @Schema(description = "故障总次数")
    private Integer faultCount;

    @Schema(description = "图片Url")
    private String imgUrl;

    @Schema(description = "所属科室名称")
    private String departmentName;

    @Schema(description = "所属区域名称")
    private String regionName;

    @Schema(description = "所属建筑名称")
    private String buildName;

    @Schema(description = "所属建筑楼层名称")
    private String buildFloorName;

    @Schema(description = "标签编码")
    private String tagCode;

    @Schema(description = "标签Id")
    private Long tagId;

    @Schema(description = "最后一此借用")
    private DetailsAssetsBorrowVo detailsAssetsBorrowVo;


}
