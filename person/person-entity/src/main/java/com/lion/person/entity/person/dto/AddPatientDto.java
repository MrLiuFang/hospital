package com.lion.person.entity.person.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.person.entity.person.Patient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"id","createDateTime", "updateDateTime", "createUserId", "updateUserId","departmentId","roomId","wardId"}
)
public class AddPatientDto extends Patient {

    @ApiModelProperty(value = "限制行动区域")
    private List<Long> regionId;

    @ApiModelProperty(value = "负责医生")
    @Column(name = "doctor_id")
    @NotNull(message = "负责医生不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Size(min = 1,message = "请选择负责的医生", groups = {Validator.Insert.class, Validator.Update.class})
    private List<Long> doctorIds;

    @ApiModelProperty(value = "负责医生")
    @Column(name = "doctor_id")
    @NotNull(message = "负责医生不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Size(min = 1,message = "请选择负责的医生", groups = {Validator.Insert.class, Validator.Update.class})
    private List<Long> nurseIds;

}
