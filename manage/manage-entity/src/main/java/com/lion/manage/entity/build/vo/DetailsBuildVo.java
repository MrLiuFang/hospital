package com.lion.manage.entity.build.vo;

import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午1:50
 */
@Data
@Schema
public class DetailsBuildVo extends Build {

    @Schema(description = "楼层")
    private List<BuildFloor> buildFloors;
}
