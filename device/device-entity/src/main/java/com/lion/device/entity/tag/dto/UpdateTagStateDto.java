package com.lion.device.entity.tag.dto;

import com.lion.core.persistence.Validator;
import com.lion.device.entity.enums.TagState;
import com.lion.device.entity.tag.Tag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午8:29
 */
@Data
@ApiModel
public class UpdateTagStateDto {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空",groups = {Validator.Update.class})
    private Long id;

    @ApiModelProperty(value = "状态")
    @NotNull(message = "状态不能为空",groups = {Validator.Update.class})
    private TagState state;
}
