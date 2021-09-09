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
        value = {"createDateTime", "updateDateTime", "createUserId", "updateUserId"}
)
public class UpdateTagRuleDto extends TagRule {

    @ApiModelProperty(value = "新增的用户")
    private List<Long> newUserIds;

    @ApiModelProperty(value = "删除的用户")
    private List<Long> deleteUserIds;

    @ApiModelProperty(value = "全部用户（全量-先删后增，此字段有值时newUserIds&deleteUserIds将无效）传空数组视为全删")
    private List<Long> allUserIds;
}
