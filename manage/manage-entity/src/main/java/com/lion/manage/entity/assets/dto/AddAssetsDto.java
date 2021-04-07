package com.lion.manage.entity.assets.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.assets.Assets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:21
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true,value = {"id","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddAssetsDto extends Assets {

    @ApiModelProperty(value = "标签Id")
    @NotNull(message = "标签不能为空")
    private Long tagId;
}
