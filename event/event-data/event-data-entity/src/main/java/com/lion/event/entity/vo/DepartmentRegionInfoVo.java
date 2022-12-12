package com.lion.event.entity.vo;

import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.vo.ListRegionVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class DepartmentRegionInfoVo {

    @Schema(description = "科室id")
    private Long departmentId;

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "区域")
    List<ListRegionVo> listRegionVos;
}
