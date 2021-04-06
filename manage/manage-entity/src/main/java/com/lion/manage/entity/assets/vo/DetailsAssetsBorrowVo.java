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

    @ApiModelProperty(value = "归还人姓名")
    private String returnUserName;
}
