package com.lion.upms.entity.user.vo;

import com.lion.upms.entity.user.UserType;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午11:28
 */
@Data
@ApiModel
public class DetailsUserTypeVo extends UserType {

    private Long userTypeId;

    public Long getUserTypeId() {
        return super.getId();
    }
}
