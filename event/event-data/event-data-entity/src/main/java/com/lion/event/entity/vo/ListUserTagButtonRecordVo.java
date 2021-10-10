package com.lion.event.entity.vo;

import com.lion.event.entity.UserTagButtonRecord;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/17 下午3:01
 */
@Data
@Schema
public class ListUserTagButtonRecordVo extends UserTagButtonRecord {

    @Schema(description = "头像（文件id）")
    private Long headPortrait;

    @Schema(description = "头像")
    private String headPortraitUrl;
}
