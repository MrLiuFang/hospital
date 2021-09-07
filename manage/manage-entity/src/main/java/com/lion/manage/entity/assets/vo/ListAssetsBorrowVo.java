package com.lion.manage.entity.assets.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.manage.entity.assets.Assets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午9:01
 */
@Data
@ApiModel
public class ListAssetsBorrowVo extends Assets {

//    @ApiModelProperty(value = "使用区域名称")
//    private String useRegionName;
//
//    @ApiModelProperty(value = "使用建筑名称")
//    private String useBuildName;
//
//    @ApiModelProperty(value = "使用建筑楼层名称")
//    private String useBuildFloorName;
//
//    @ApiModelProperty(value = "使用科室名称")
//    private String useDepartmentName;

    @ApiModelProperty(value = "所属区域名称")
    private String regionName;

    @ApiModelProperty(value = "所属建筑名称")
    private String buildName;

    @ApiModelProperty(value = "所属建筑楼层名称")
    private String buildFloorName;

    @ApiModelProperty(value = "所属科室名称")
    private String departmentName;

    @ApiModelProperty(value = "标签码")
    private String tagCode;

    @ApiModelProperty(value = "资产借用id")
    private Long assetsBorrowId;

    @ApiModelProperty(value = "借用科室Id")
    private Long borrowDepartmentId;

    @ApiModelProperty(value = "借用床位Id")
    private Long borrowWardRoomSickbedId;

    @ApiModelProperty(value = "借用科室名称")
    private String borrowDepartmentName;

    @ApiModelProperty(value = "借用床位编号")
    private String borrowWardRoomSickbedCode;

    @ApiModelProperty(value = "借用开始时间(yyyy-MM-dd HH:mm:ss)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;

    @ApiModelProperty(value = "借用结束时间(yyyy-MM-dd HH:mm:ss)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    @ApiModelProperty(value = "借用人姓名")
    private String borrowUserName;

    @ApiModelProperty(value = "借用人头像Id")
    private Long borrowUserHeadPortrait;

    @ApiModelProperty(value = "借用人头像")
    private String borrowUserHeadPortraitUrl;

    @ApiModelProperty(value = "归还人姓名")
    private String returnUserName;

    @ApiModelProperty(value = "归还人头像Id")
    private Long returnUserHeadPortrait;

    @ApiModelProperty(value = "归还人头像")
    private String returnUserHeadPortraitUrl;

    @ApiModelProperty(value = "登记时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registrationTime;

    @ApiModelProperty(value = "归还时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime returnTime = LocalDateTime.now();
}
