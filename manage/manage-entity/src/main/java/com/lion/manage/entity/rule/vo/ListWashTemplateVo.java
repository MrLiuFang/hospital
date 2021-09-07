package com.lion.manage.entity.rule.vo;

import com.lion.manage.entity.enums.WashDeviceType;
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
public class ListWashTemplateVo extends WashTemplate {

    @ApiModelProperty(value = "规则项")
    List<ListWashTemplateItemVo> listWashTemplateItemVos;

    @ApiModelProperty(value = "创建人")
    private String createUserName;

    @ApiModelProperty(value = "创建人头像")
    private Long createUserHeadPortrait;

    @ApiModelProperty(value = "创建人头像url")
    private String createUserHeadPortraitUrl;
}
