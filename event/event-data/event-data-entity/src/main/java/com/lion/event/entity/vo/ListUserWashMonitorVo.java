package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.upms.entity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //手卫生监控
 * @Date 2021/5/11 下午4:43
 **/
@Data
@Schema
public class ListUserWashMonitorVo {

    @Schema(description = "姓名")
    private String userName;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "头像ID")
    private Long headPortrait;

    @Schema(description = "头像")
    private String headPortraitUrl;

    @Schema(description = "用户类型")
    private UserType userType;

//    @Schema(description = "是否有洗手规则")
//    private Boolean isExistWashRule = true;

    @Schema(description = "合规")
    private BigDecimal conformance = new BigDecimal(0);

    @Schema(description = "违规")
    private BigDecimal violation = new BigDecimal(0);

    @Schema(description = "错过洗手")
    private BigDecimal noWash =new BigDecimal(0);

    @Schema(description = "所有事件(合规+不合规)")
    private BigDecimal allCount;

    public BigDecimal getAllCount() {
        return conformance.add(violation).add(noWash);
    }

    @Schema(description = "上班时间")
    @JsonFormat(
            pattern = "YYYY-MM-dd HH:mm:ss"
    )
    private LocalDateTime startWorkTime;

    @Schema(description = "下班时间")
    @JsonFormat(
            pattern = "YYYY-MM-dd HH:mm:ss"
    )
    private LocalDateTime endWorkTime;
}
