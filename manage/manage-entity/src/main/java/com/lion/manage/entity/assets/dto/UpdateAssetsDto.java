package com.lion.manage.entity.assets.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.assets.Assets;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:21
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"buildId","buildFloorId","createDateTime","updateDateTime","createUserId","updateUserId"})
public class UpdateAssetsDto extends Assets {

    @Schema(description = "标签编码")
    @NotBlank(message = "标签编码不能为空")
    private String tagCode;
}
