package com.lion.device.entity.device.vo;

import com.lion.device.entity.device.WarningBell;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 下午2:04
 */
@Data
@Schema
public class DetailsWarningBellVo extends WarningBell {

    @Schema(description = "图片Url")
    private String imgUrl;

    @Schema(description = "所属区域名称")
    private String regionName;

    @Schema(description = "所属科室名称")
    private String departmentName;
}
