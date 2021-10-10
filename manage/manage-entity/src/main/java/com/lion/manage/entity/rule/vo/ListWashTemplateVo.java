package com.lion.manage.entity.rule.vo;

import com.lion.manage.entity.enums.WashDeviceType;
import com.lion.manage.entity.rule.WashTemplate;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午11:25
 */
@Data
@Schema
public class ListWashTemplateVo extends WashTemplate {

    @Schema(description = "规则项")
    List<ListWashTemplateItemVo> listWashTemplateItemVos;

    @Schema(description = "创建人")
    private String createUserName;

    @Schema(description = "创建人头像")
    private Long createUserHeadPortrait;

    @Schema(description = "创建人头像url")
    private String createUserHeadPortraitUrl;
}
