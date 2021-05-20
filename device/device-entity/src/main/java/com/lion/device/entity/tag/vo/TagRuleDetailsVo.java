package com.lion.device.entity.tag.vo;

import com.lion.device.entity.tag.TagRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/20 下午8:35
 */
@Data
@ApiModel
public class TagRuleDetailsVo extends TagRule {

    @ApiModelProperty(value = "所以的用户id")
    private List<Long> allUserIds;
}
