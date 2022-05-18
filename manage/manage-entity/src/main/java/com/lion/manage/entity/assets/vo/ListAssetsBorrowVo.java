package com.lion.manage.entity.assets.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.AssetsType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午9:01
 */
@Data
@Schema
public class ListAssetsBorrowVo extends Assets {

//    @Schema(description = "使用区域名称")
//    private String useRegionName;
//
//    @Schema(description = "使用建筑名称")
//    private String useBuildName;
//
//    @Schema(description = "使用建筑楼层名称")
//    private String useBuildFloorName;
//
//    @Schema(description = "使用科室名称")
//    private String useDepartmentName;

    @Schema(description = "所属区域名称")
    private String regionName;

    @Schema(description = "所属建筑名称")
    private String buildName;

    @Schema(description = "所属建筑楼层名称")
    private String buildFloorName;

    @Schema(description = "所属科室名称")
    private String departmentName;

    @Schema(description = "标签码")
    private String tagCode;

    @Schema(description = "资产借用id")
    private Long assetsBorrowId;

    @Schema(description = "借用科室Id")
    private Long borrowDepartmentId;

    @Schema(description = "借用床位Id")
    private Long borrowWardRoomSickbedId;

    @Schema(description = "借用科室名称")
    private String borrowDepartmentName;

    @Schema(description = "借用床位编号")
    private String borrowWardRoomSickbedCode;

    @Schema(description = "借用开始时间(yyyy-MM-dd HH:mm:ss)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;

    @Schema(description = "借用结束时间(yyyy-MM-dd HH:mm:ss)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    @Schema(description = "借用人姓名")
    private String borrowUserName;

    @Schema(description = "借用人头像Id")
    private Long borrowUserHeadPortrait;

    @Schema(description = "借用人头像")
    private String borrowUserHeadPortraitUrl;

    @Schema(description = "归还人姓名")
    private String returnUserName;

    @Schema(description = "归还人头像Id")
    private Long returnUserHeadPortrait;

    @Schema(description = "归还人头像")
    private String returnUserHeadPortraitUrl;

    @Schema(description = "登记时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registrationTime;

    @Schema(description = "归还时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime returnTime = LocalDateTime.now();

    @Schema(description = "资产类型")
    private AssetsType assetsType;
}
