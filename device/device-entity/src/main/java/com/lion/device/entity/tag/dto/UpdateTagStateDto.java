package com.lion.device.entity.tag.dto;

import com.lion.core.persistence.Validator;
import com.lion.device.entity.enums.TagState;
import com.lion.device.entity.tag.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午8:29
 */
@Data
@Schema
public class UpdateTagStateDto {

    @Schema(description = "id")
    @NotNull(message = "{0000000}",groups = {Validator.Update.class})
    private Long id;

    @Schema(description = "状态")
    @NotNull(message = "{1000030}",groups = {Validator.Update.class})
    private TagState state;
}
