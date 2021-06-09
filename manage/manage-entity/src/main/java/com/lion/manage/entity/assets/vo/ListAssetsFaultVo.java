package com.lion.manage.entity.assets.vo;

import com.lion.core.persistence.Validator;
import com.lion.manage.entity.assets.AssetsFault;
import com.lion.manage.entity.enums.AssetsType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午8:57
 */
@Data
@ApiModel
public class ListAssetsFaultVo extends AssetsFault {

    @ApiModelProperty(value = "申报人姓名")
    private String declarantUserName;

    @ApiModelProperty(value = "申报人头像")
    private Long declarantUserHeadPortrait;

    @ApiModelProperty(value = "申报人头像")
    private String declarantUserHeadPortraitUrl;

    @ApiModelProperty(value = "资产图片")
    private Long img;

    @ApiModelProperty(value = "资产图片")
    private String imgUrl;

    @ApiModelProperty(value = "资产名称")
    private String name;

    @ApiModelProperty(value = "资产编号")
    private String code;

    @ApiModelProperty(value = "所属区域")
    private String regionName;

    @ApiModelProperty(value = "所属建筑")
    private String buildName;

    @ApiModelProperty(value = "所属建筑楼层")
    private String buildFloorName;

    @ApiModelProperty(value = "所属科室")
    private String departmentName;
}
