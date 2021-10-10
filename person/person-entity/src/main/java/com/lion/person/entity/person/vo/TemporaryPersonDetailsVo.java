package com.lion.person.entity.person.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.person.entity.person.TemporaryPerson;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午4:10
 */
@Data
@Schema
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"updateDateTime", "createUserId", "updateUserId"}
)
public class TemporaryPersonDetailsVo extends TemporaryPerson {

    @Schema(description = "头像")
    private String headPortraitUrl;

    @Schema(description = "电量(0=正常,1=少於90 天,2=少於30天)")
    private Integer battery;

    @Schema(description = "科室名称（来源于拜访人）")
    private String departmentName;

//    @Schema(description = "限制区域")
//    private List<TemporaryPersonDetailsVo.RestrictedAreaVo> restrictedAreaVoList;

    @Schema(description = "警告")
    private String alarm;

    @Schema(description = "警告编码")
    private String alarmType;

    @Schema(description = "警告时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmDataTime;

    @Schema(description = "警报ID")
    private String alarmId;

//    @Data
//    @Schema
//    public static class RestrictedAreaVo{
//
//        @Schema(description = "区域id")
//        private Long regionId;
//
//        @Schema(description = "区域名称")
//        private String regionName;
//
//        @Schema(description = "建筑名称")
//        private String buildName;
//
//        @Schema(description = "建筑楼成名称")
//        private String buildFloorName;
//
//        @Schema(description = "备注")
//        private String remark;
//    }


}
