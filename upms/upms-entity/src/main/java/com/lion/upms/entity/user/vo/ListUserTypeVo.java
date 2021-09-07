package com.lion.upms.entity.user.vo;

import com.google.common.collect.PeekingIterator;
import com.lion.upms.entity.user.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/6 下午9:35
 */
@Data
@ApiModel
public class ListUserTypeVo extends UserType {

    @ApiModelProperty(value = "用户数量")
    private Integer userCount;

    private Long userTypeId;

    public Long getUserTypeId() {
        return super.getId();
    }

}
