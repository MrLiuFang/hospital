package com.lion.manage.entity.assets.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.assets.AssetsBorrow;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午8:37
 */
@Data
@Schema
public class ReturnAssetsBorrowDto {

    @Schema(description = "资产借用id(不是资产id)assetsBorrowId")
    @NotNull(message = "{0000000}")
    private List<Long> assetsBorrowIds;

    @Schema(description = "归还人编号")
    @NotNull(message = "{2000020}")
    private Integer number;
}
