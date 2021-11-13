package com.lion.manage.entity.region.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class BatchUpdateWashTemplateDto {

    @Schema(description = "洗手规则模板")
    private Long washTemplateId;

    @Schema(description = "区域ID")
    private List<Long> regionIds;
}
