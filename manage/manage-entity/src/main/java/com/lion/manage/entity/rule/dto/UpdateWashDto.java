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
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
public class UpdateWashDto extends Wash {

    @ApiModelProperty(value = "区域id（全量，先删后增）")
    private List<Long> regionId;

    @ApiModelProperty(value = "用户id（全量，先删后增）")
    private List<Long> userId;

    @ApiModelProperty(value = "洗手设备类型（全量，先删后增）")
    private List<WashDeviceType> deviceType;
}
