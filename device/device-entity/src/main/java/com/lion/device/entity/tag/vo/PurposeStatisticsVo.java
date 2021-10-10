package com.lion.device.entity.tag.vo;

import com.lion.core.persistence.Validator;
import com.lion.device.entity.enums.TagPurpose;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema
public class PurposeStatisticsVo {

    @Schema(description = "标签用途")
    private TagPurpose purpose;

    @Schema(description = "已用数量")
    private int count;

    @Schema(description = "剩余数量-空着，目前计算不出这个值")
    private int surplusCount;


}

