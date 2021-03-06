package com.lion.manage.entity.rule.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.manage.entity.rule.WashTemplate;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午11:05
 */
@Data
@Schema
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"createDateTime", "updateDateTime", "createUserId", "updateUserId"}
)
public class UpdateWashTemplateDto extends WashTemplate {

    @Schema(description = "规则项(全量-先删后增)")
    @Size(min = 2,max = 2,message = "{2000103}",groups = {Validator.Insert.class})
    @NotNull(message = "{2000103}",groups = {Validator.Insert.class})
    @JsonIgnoreProperties(
            ignoreUnknown = true,
            value = {"id","createDateTime", "updateDateTime", "createUserId", "updateUserId"}
    )
    private List<AddWashTemplateItemDto> washTemplateItems;
}
