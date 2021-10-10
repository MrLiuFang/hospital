package com.lion.person.entity.person.vo;

import com.lion.core.persistence.Validator;
import com.lion.person.entity.person.Patient;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午2:16
 */
@Data
@Schema
public class ListPatientVo extends PatientDetailsVo {

    @Schema(description = "离开时间")
    private LocalDateTime leaveDateTime;

    @Schema(description = "转移触发时间")
    private LocalDateTime triggerDateTime;


}
