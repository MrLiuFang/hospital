package com.lion.device.entity.tag.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.tag.TagRule;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 上午11:09
 **/
@Data
@ApiModel
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"createDateTime", "updateDateTime", "createUserId", "updateUserId"}
)
public class UpdateTagRuleDto extends TagRule {

    /**
     * 关联的用户(新加)
     */
    private List<Long> newUserIds;

    /**
     * 删除的用户
     */
    private List<Long> deleteUserIds;
}
