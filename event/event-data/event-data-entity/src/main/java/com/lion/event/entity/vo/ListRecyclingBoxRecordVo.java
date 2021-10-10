package com.lion.event.entity.vo;

import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.event.entity.RecyclingBoxRecord;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 下午3:11
 */
@Data
@Schema
public class ListRecyclingBoxRecordVo extends RecyclingBoxRecord {

    @Schema(description = "tag用途")
    private TagPurpose tagPurpose;

    @Schema(description = "tag类型")
    private TagType tagType;
}
