package com.lion.manage.entity.assets.vo;

import com.lion.manage.entity.assets.Assets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:40
 */
@Data
@ApiModel
public class DetailsAssetsVo extends Assets {

    @ApiModelProperty(value = "位置")
    private String position;

    @ApiModelProperty(value = "借用总次数")
    private Integer borrowCount;

    @ApiModelProperty(value = "故障总次数")
    private Integer faultCount;

    @ApiModelProperty(value = "图片Url")
    private String imgUrl;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;
}
