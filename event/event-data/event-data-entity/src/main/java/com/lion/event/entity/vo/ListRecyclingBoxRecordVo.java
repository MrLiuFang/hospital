package com.lion.event.entity.vo;

import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.event.entity.RecyclingBoxRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 下午3:11
 */
@Data
@ApiModel
public class ListRecyclingBoxRecordVo extends RecyclingBoxRecord {

    @ApiModelProperty(value = "tag用途")
    private TagPurpose tagPurpose;

    @ApiModelProperty(value = "tag类型")
    private TagType tagType;
}
