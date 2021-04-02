package com.lion.manage.entity.ward.vo;

import com.lion.manage.entity.ward.Ward;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/2上午11:29
 */
@Data
@ApiModel
public class ListWardVo extends Ward {

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "房间数量")
    private Integer roomQuantity = 0;

    @ApiModelProperty(value = "病床数量")
    private Integer sickbedQuantity = 0;
}
