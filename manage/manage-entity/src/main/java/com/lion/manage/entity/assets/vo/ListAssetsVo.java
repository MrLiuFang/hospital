package com.lion.manage.entity.assets.vo;

import com.lion.manage.entity.assets.Assets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午7:44
 */
@Data
@ApiModel
public class ListAssetsVo extends Assets {

    @ApiModelProperty(value = "位置")
    private String position;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "标签码")
    private String tagCode;
}
