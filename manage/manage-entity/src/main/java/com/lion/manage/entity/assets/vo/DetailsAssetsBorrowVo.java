package com.lion.manage.entity.assets.vo;

import com.lion.manage.entity.assets.AssetsBorrow;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午9:01
 */
@Data
@Schema
public class DetailsAssetsBorrowVo extends AssetsBorrow {

    @Schema(description = "借用人姓名")
    private String borrowUserName;

    @Schema(description = "借用人头像URl")
    private String borrowUserHeadPortraitUrl;

    @Schema(description = "借用人头像")
    private Long borrowUserHeadPortrait;

    @Schema(description = "借用科室")
    private String borrowDepartmentName;

    @Schema(description = "归还人姓名")
    private String returnUserName;

    @Schema(description = "归还人头像Url")
    private String returnUserHeadPortraitUrl;

    @Schema(description = "归还人头像")
    private Long returnUserHeadPort;
}
