package com.lion.manage.entity.assets.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.assets.Assets;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:21
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"id","deviceState","lastDataTime","buildId","buildFloorId","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddAssetsDto extends Assets {

    @Schema(description = "标签编码")
    private String tagCode;
}
