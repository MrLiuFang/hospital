package com.lion.manage.entity.assets.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.assets.AssetsType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午9:35
 */
@Data
@Schema
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"createDateTime", "updateDateTime", "createUserId", "updateUserId"}
)
public class UpdateAssetsTypeDto extends AssetsType {
}
