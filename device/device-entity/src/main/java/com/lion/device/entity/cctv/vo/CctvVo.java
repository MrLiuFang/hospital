package com.lion.device.entity.cctv.vo;

import com.lion.device.entity.cctv.Cctv;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/7 上午8:48
 **/
@Data
@ApiModel
public class CctvVo extends Cctv {

    @ApiModelProperty(value = "建筑名称")
    private String buildName;

    @ApiModelProperty(value = "楼层名称")
    private String buildFloorName;

    @ApiModelProperty(value = "区域名称")
    private String regionName;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;
}
