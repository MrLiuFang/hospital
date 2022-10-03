package com.lion.device.entity.cctv.vo;

import com.lion.device.entity.cctv.Cctv;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/7 上午8:48
 **/
@Data
@Schema
public class CctvVo extends Cctv {

    @Schema(description = "建筑名称")
    private String buildName;

    @Schema(description = "楼层名称")
    private String buildFloorName;

    @Schema(description = "区域名称")
    private String regionName;

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "图片")
    private String imgUrl;

    private String createUserName;

    private String updateUserName;

}
