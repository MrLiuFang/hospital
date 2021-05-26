package com.lion.manage.entity.assets.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.assets.AssetsBorrow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午8:37
 */
@Data
@ApiModel
public class ReturnAssetsBorrowDto {

    @ApiModelProperty(value = "资产借用id")
    @NotNull(message = "id不能为空")
    private Long assetsBorrowId;

    @ApiModelProperty(value = "归还人编号")
    @NotNull(message = "归还人编号不能为空")
    private Integer number;
}
