package com.lion.manage.entity.region.vo;

import com.lion.core.persistence.Validator;
import com.lion.device.entity.device.Device;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionType;
import com.lion.manage.entity.rule.vo.DetailsWashTemplateVo;
import com.lion.manage.entity.rule.vo.ListWashTemplateVo;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/14 上午11:43
 **/
@Data
@Schema
public class ListRegionVo extends Region {

    @Schema(description = "建筑")
    private String buildName;

    @Schema(description = "建筑楼层")
    private String buildFloorName;

    @Schema(description = "科室")
    private String departmentName;

    @Schema(description = "洗手规则")
    private DetailsWashTemplateVo washTemplateVo;

    @Schema(description = "区域类型")
    private RegionType regionType;

    @Schema(description = "区域里的洗手设备")
    private List<Device> devices;

}
