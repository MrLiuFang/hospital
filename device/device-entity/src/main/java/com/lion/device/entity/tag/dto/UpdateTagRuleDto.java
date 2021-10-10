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
        value = {"createDateTime", "updateDateTime", "createUserId", "updateUserId"}
)
public class UpdateTagRuleDto extends TagRule {

    @Schema(description = "新增的用户")
    private List<Long> newUserIds;

    @Schema(description = "删除的用户")
    private List<Long> deleteUserIds;

    @Schema(description = "全部用户（全量-先删后增，此字段有值时newUserIds&deleteUserIds将无效）传空数组视为全删")
    private List<Long> allUserIds;
}
