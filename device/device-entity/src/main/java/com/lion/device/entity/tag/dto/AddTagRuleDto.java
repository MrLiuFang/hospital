package com.lion.device.entity.tag.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.tag.TagRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午11:09
 **/
@Data
@ApiModel
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"id","createDateTime", "updateDateTime", "createUserId", "updateUserId"}
)
public class AddTagRuleDto extends TagRule {

    /**
     * 关联的用户
     */
    @ApiModelProperty(value = "关联的用户ID")
    private List<Long> userIds;
}
