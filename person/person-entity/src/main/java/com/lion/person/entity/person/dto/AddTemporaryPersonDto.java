package com.lion.person.entity.person.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.person.entity.person.TemporaryPerson;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:25
 */
@Data
@Schema
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"id","deviceState","lastDataTime","isLeave","isWaitLeave","createDateTime", "updateDateTime", "createUserId", "updateUserId"}
)
public class AddTemporaryPersonDto extends TemporaryPerson {
    @Schema(description = "限制行动区域")
    private List<Long> regionId;
}
