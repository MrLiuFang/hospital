package com.lion.manage.entity.assets.vo;

import com.lion.manage.entity.assets.AssetsFault;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午8:57
 */
@Data
@Schema
public class ListAssetsFaultVo extends AssetsFault {

    @Schema(description = "申报人姓名")
    private String declarantUserName;

    @Schema(description = "申报人头像")
    private Long declarantUserHeadPortrait;

    @Schema(description = "申报人头像")
    private String declarantUserHeadPortraitUrl;

    @Schema(description = "资产图片")
    private Long img;

    @Schema(description = "资产图片")
    private String imgUrl;

    @Schema(description = "资产名称")
    private String name;

    @Schema(description = "资产编号")
    private String deviceCode;

    @Schema(description = "所属区域")
    private String regionName;

    @Schema(description = "所属建筑")
    private String buildName;

    @Schema(description = "所属建筑楼层")
    private String buildFloorName;

    @Schema(description = "所属科室")
    private String departmentName;
}
