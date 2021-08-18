package com.lion.device.entity.tag.vo;

import com.lion.core.persistence.Validator;
import com.lion.device.entity.enums.TagPurpose;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2021-08-18 13:54
 **/
@Data
@Builder
@ApiModel
public class PurposeStatisticsVo {

    @ApiModelProperty(value = "标签用途")
    private TagPurpose purpose;

    @ApiModelProperty(value = "已用数量")
    private int count;

    @ApiModelProperty(value = "剩余数量-空着，目前计算不出这个值")
    private int surplusCount;


}

