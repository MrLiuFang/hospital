package com.lion.manage.entity.assets.vo;

import com.lion.manage.entity.assets.AssetsType;
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
public class ListAssetsTypeVo extends AssetsType {

    @ApiModelProperty(value = "资产数量")
    private Integer assetsCount;

    private Long assetsTypeId;

    public Long getAssetsTypeId() {
        return super.getId();
    }

}
