package com.lion.manage.entity.rule.dto;

import com.lion.manage.entity.enums.WashDeviceType;
import com.lion.manage.entity.rule.WashTemplateItem;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午10:58
 */
@Data
@Schema
public class AddWashTemplateItemDto extends WashTemplateItem {

    @Schema(description = "洗手设备类型(ISINFECTION_GEL(0, \"免洗消毒凝胶\"),LIQUID_SOAP(1, \"洗手液\"),ALCOHOL(2, \"酒精\"),WASHING_FOAM(3, \"洗手泡沫\"), WATER(4, \"清水\"))")
    private List<WashDeviceType> deviceTypes;
}
