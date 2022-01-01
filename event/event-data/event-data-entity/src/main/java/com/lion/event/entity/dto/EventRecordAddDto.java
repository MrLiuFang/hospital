package com.lion.event.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema
public class EventRecordAddDto {

    @Schema(description = "扩展字段-自由发挥")
    private String extend;

//    @Schema(description = "区域id")
//    private Long regionId;

    @Schema(description = "事件编号")
    private String code;

    @Schema(description = "备注")
    private String remarks;

//    @Schema(description = "开始时间(yyyy-MM-dd HH:mm:ss)")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime startDateTime;
//
//    @Schema(description = "结束时间(yyyy-MM-dd HH:mm:ss)")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    LocalDateTime endDateTime;
}
