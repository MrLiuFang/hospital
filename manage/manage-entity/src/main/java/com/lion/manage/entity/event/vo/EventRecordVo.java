package com.lion.manage.entity.event.vo;

import com.lion.manage.entity.event.EventRecord;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/29 上午10:08
 */
@Data
@Schema
public class EventRecordVo extends EventRecord {

    @Schema(description = "创建人姓名")
    private String createUserName;

    @Schema(description = "创建人头像")
    private Long headPortrait;

    @Schema(description = "创建人头像url")
    private String headPortraitUrl;
}
