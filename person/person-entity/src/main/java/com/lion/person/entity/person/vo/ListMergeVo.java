package com.lion.person.entity.person.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.person.entity.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.time.LocalDateTime;

@Schema
@Data
public class ListMergeVo {

    @Schema(
            description = "id"
    )
    private Long id;

    @Schema(
            description = "姓名"
    )
    private String name;
    @Schema(
            description = "头像"
    )
    private Long headPortrait;
    @Schema(
            description = "1:患者，2:流动人员"
    )
    private Integer type;
    @Schema(
            description = "标签id"
    )
    private String tagCode;
    @Schema(
            description = "登记时间"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime createDateTime;

    @Schema(description = "性别")
    private Gender gender;
}
