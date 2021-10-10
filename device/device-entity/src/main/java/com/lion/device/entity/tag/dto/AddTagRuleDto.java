package com.lion.device.entity.tag.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.tag.TagRule;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午11:09
 **/
@Data
@Schema
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"id","createDateTime", "updateDateTime", "createUserId", "updateUserId"}
)
public class AddTagRuleDto extends TagRule {

    /**
     * 关联的用户
     */
    @Schema(description = "关联的用户ID")
    private List<Long> userIds;
}
