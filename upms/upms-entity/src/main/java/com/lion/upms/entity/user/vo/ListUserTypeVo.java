package com.lion.upms.entity.user.vo;

import com.google.common.collect.PeekingIterator;
import com.lion.upms.entity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/6 下午9:35
 */
@Data
@Schema
public class ListUserTypeVo extends UserType {

    @Schema(description = "用户数量")
    private Integer userCount;

    private Long userTypeId;

    public Long getUserTypeId() {
        return super.getId();
    }

}
