package com.lion.manage.entity.license;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Schema
@Data
public class AboutVo {

    @Schema(description = "授权开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "授权结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "激活时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate activeTime;

    @Schema(description = "授权工作站")
    private int workstation;

    @Schema(description = "授权用户数")
    private int userNum;

    @Schema(description = "网络设备-无数据")
    private int cctvNum;

    @Schema(description = "标签设备-无数据")
    private int tagNum;

    @Schema(description = "授权菜单")
    private String menuList;
}
