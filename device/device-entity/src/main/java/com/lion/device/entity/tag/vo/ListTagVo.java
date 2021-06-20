package com.lion.device.entity.tag.vo;

import com.lion.device.entity.tag.Tag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 上午9:18
 **/
@Data
@ApiModel
public class ListTagVo extends Tag {

    @ApiModelProperty(value = "绑定对象")
    private String bindingName;

    @ApiModelProperty(value = "科室")
    private String departmentName;
}
