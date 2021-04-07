package com.lion.manage.entity.assets.dto;

import com.lion.manage.entity.assets.Assets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:21
 */
@Data
@ApiModel
public class UpdateAssetsDto extends Assets {

    @ApiModelProperty(value = "标签Id")
    @NotNull(message = "标签不能为空")
    private Long tagId;
}
