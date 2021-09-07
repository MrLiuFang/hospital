package com.lion.manage.entity.rule.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.manage.entity.rule.WashTemplate;
import com.lion.manage.entity.rule.WashTemplateItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午10:56
 */
@Data
@ApiModel
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"id","createDateTime", "updateDateTime", "createUserId", "updateUserId"}
)
public class AddWashTemplateDto extends WashTemplate {

    @ApiModelProperty(value = "规则项")
    @Size(min = 2,max = 2,message = "{2000103}",groups = {Validator.Insert.class})
    @NotNull(message = "{2000103}",groups = {Validator.Insert.class})
    private List<AddWashTemplateItemDto> washTemplateItems;
}
