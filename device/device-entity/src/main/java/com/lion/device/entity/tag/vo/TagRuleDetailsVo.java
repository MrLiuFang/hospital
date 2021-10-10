package com.lion.device.entity.tag.vo;

import com.lion.device.entity.tag.TagRule;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/20 下午8:35
 */
@Data
@Schema
public class TagRuleDetailsVo extends TagRule {

    @Schema(description = "所以的用户id")
    private List<Long> allUserIds;
}
