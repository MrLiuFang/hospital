package com.lion.manage.entity.assets.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.assets.AssetsType;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午9:35
 */
@Data
@ApiModel
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"id","createDateTime", "updateDateTime", "createUserId", "updateUserId"}
)
public class AddAssetsTypeDto extends AssetsType {
}
