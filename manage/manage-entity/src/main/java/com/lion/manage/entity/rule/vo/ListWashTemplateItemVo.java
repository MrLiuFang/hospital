package com.lion.manage.entity.rule.vo;

import com.lion.manage.entity.enums.WashDeviceType;
import com.lion.manage.entity.rule.WashTemplateItem;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午11:36
 */
@Data
@Schema
public class ListWashTemplateItemVo extends WashTemplateItem {

    @Schema(description = "洗手设备类型")
    List<WashDeviceType> washDeviceTypes;
}
