package com.lion.manage.entity.rule.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.enums.WashDeviceType;
import com.lion.manage.entity.rule.Wash;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午5:07
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true,value = {"id","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddWashDto extends Wash {

    @ApiModelProperty(value = "区域id")
    private List<Long> regionId;

    @ApiModelProperty(value = "用户id")
    private List<Long> userId;

    @ApiModelProperty(value = "洗手设备类型(ISINFECTION_GEL(0, \"免洗消毒凝胶\"),LIQUID_SOAP(1, \"洗手液\"),ALCOHOL(2, \"酒精\"),WASHING_FOAM(3, \"洗手泡沫\"), WATER(4, \"清水\"))")
    private List<WashDeviceType> deviceType;
}
