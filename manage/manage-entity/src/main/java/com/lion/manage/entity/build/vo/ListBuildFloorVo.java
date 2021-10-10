package com.lion.manage.entity.build.vo;

import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.region.Region;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午2:25
 */
@Data
@Schema
public class ListBuildFloorVo extends BuildFloor {

    @Schema(description = "所属建筑")
    private Build build;

    @Schema(description = "该楼层所有区域(某些功能模块会使用)")
    private List<Region> regions;
}
