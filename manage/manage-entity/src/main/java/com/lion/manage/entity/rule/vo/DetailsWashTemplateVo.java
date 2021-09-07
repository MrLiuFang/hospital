package com.lion.manage.entity.rule.vo;

import com.lion.manage.entity.rule.WashTemplate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午11:25
 */
@Data
@ApiModel
public class DetailsWashTemplateVo extends WashTemplate {

    @ApiModelProperty(value = "规则项")
    List<ListWashTemplateItemVo> listWashTemplateItemVos;
}
