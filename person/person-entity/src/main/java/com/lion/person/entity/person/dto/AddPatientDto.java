package com.lion.person.entity.person.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.person.entity.person.Patient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
}
