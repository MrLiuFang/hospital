package com.lion.manage.entity.assets.vo;

import com.lion.manage.entity.assets.AssetsFault;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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

//    @ApiModelProperty(value = "完成人姓名")
//    private String finishUserName;
}
