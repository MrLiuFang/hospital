package com.lion.person.entity.person.vo;

import com.lion.person.entity.person.TemporaryPerson;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午4:10
 */
@Data
@ApiModel
public class TemporaryPersonDetailsVo extends TemporaryPerson {

    @ApiModelProperty(value = "头像")
    private String headPortraitUrl;
}
