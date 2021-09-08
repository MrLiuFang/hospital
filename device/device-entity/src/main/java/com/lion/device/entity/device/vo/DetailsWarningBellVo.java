package com.lion.device.entity.device.vo;

import com.lion.device.entity.device.WarningBell;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 下午2:04
 */
@Data
@ApiModel
public class DetailsWarningBellVo extends WarningBell {

    @ApiModelProperty(value = "图片Url")
    private String imgUrl;

    @ApiModelProperty(value = "所属区域名称")
    private String regionName;

    @ApiModelProperty(value = "所属科室名称")
    private String departmentName;
}
