package com.lion.person.entity.person.vo;

import com.lion.person.entity.person.PatientLog;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 下午4:24
 */
@Data
@Schema
public class ListPatientLogVo extends PatientLog {

    @Schema(description = "操作人姓名")
    private String userName;

    @Schema(description = "操作人头像")
    private Long userHeadPortrait;

    @Schema(description = "操作人头像")
    private String userHeadPortraitUrl;
}
