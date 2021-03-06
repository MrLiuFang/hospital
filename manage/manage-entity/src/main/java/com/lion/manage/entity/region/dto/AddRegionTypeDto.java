package com.lion.manage.entity.region.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.region.RegionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 上午8:52
 */
@Data
@Schema
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"id","createDateTime", "updateDateTime", "createUserId", "updateUserId"}
)
public class AddRegionTypeDto extends RegionType {
}
