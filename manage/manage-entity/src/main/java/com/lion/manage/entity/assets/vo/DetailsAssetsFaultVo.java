package com.lion.manage.entity.assets.vo;

import com.lion.manage.entity.assets.AssetsFault;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午9:00
 */
@Data
@ApiModel
public class DetailsAssetsFaultVo extends AssetsFault {

    @ApiModelProperty(value = "申报人姓名")
    private String declarantUserName;
}
