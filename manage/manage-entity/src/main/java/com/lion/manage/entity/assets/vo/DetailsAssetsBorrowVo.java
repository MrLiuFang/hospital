package com.lion.manage.entity.assets.vo;

import com.lion.manage.entity.assets.AssetsBorrow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午9:01
 */
@Data
@ApiModel
public class DetailsAssetsBorrowVo extends AssetsBorrow {

    @ApiModelProperty(value = "借用人姓名")
    private String borrowUserName;

    @ApiModelProperty(value = "借用人头像URl")
    private String borrowUserHeadPortraitUrl;

    @ApiModelProperty(value = "借用人头像")
    private Long borrowUserHeadPortrait;

    @ApiModelProperty(value = "借用科室")
    private String borrowDepartmentName;

    @ApiModelProperty(value = "归还人姓名")
    private String returnUserName;

    @ApiModelProperty(value = "归还人头像Url")
    private String returnUserHeadPortraitUrl;

    @ApiModelProperty(value = "归还人头像")
    private Long returnUserHeadPort;
}
