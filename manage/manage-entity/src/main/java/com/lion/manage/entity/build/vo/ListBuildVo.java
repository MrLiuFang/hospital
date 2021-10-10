package com.lion.manage.entity.build.vo;

import com.lion.manage.entity.build.Build;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午2:25
 */
@Data
@Schema
public class ListBuildVo extends Build {

    @Schema(description = "楼层数")
    private Integer totalFloors;
}
