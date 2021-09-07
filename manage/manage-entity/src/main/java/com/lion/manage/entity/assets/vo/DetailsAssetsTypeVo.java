package com.lion.manage.entity.assets.vo;

import com.lion.manage.entity.assets.AssetsType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午11:30
 */
@Data
@ApiModel
public class DetailsAssetsTypeVo extends AssetsType {

    private Long assetsTypeId;

    public Long getAssetsTypeId() {
        return super.getId();
    }

    @ApiModelProperty(value = "规则项")
    private List<ListWashTemplateItemVo> listWashTemplateItemVos;
}
