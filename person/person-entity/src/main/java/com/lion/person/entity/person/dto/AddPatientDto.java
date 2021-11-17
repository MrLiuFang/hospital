package com.lion.person.entity.person.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.person.entity.person.Patient;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:24
 */
@Data
@Schema
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"id","deviceState","lastDataTime","isLeave","isWaitLeave","createDateTime", "updateDateTime", "createUserId", "updateUserId","departmentId","roomId","wardId"}
)
public class AddPatientDto extends Patient {

//    @Schema(description = "限制行动区域")
//    private List<Long> regionId;

//    @Schema(description = "负责医生")
//    @Column(name = "doctor_id")
//    @NotNull(message = "{1000006}", groups = {Validator.Insert.class, Validator.Update.class})
//    @Size(min = 1,message = "{1000006}", groups = {Validator.Insert.class, Validator.Update.class})
//    private List<Long> doctorIds;
//
//    @Schema(description = "负责护士")
//    @Column(name = "doctor_id")
//    @NotNull(message = "{1000008}", groups = {Validator.Insert.class, Validator.Update.class})
//    @Size(min = 1,message = "{1000008}", groups = {Validator.Insert.class, Validator.Update.class})
//    private List<Long> nurseIds;

}
