package com.lion.manage.entity.rule.vo;

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
public class DetailsWashTemplateVo extends WashTemplate {

    @Schema(description = "规则项")
    List<ListWashTemplateItemVo> listWashTemplateItemVos;
}
