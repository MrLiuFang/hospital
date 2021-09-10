package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.upms.entity.user.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel
public class ListUserWashMonitorVo {

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "头像ID")
    private Long headPortrait;

    @ApiModelProperty(value = "头像")
    private String headPortraitUrl;

    @ApiModelProperty(value = "用户类型")
    private UserType userType;

    @ApiModelProperty(value = "是否有洗手规则")
    private Boolean isExistWashRule = true;

    @ApiModelProperty(value = "合规率")
    private BigDecimal conformance = new BigDecimal(0);

    @ApiModelProperty(value = "违规")
    private BigDecimal violation = new BigDecimal(0);

    @ApiModelProperty(value = "错过洗手")
    private BigDecimal noWash =new BigDecimal(0);

    @ApiModelProperty(value = "上班时间")
    @JsonFormat(
            pattern = "YYYY-MM-dd HH:mm:ss"
    )
    private LocalDateTime startWorkTime;

    @ApiModelProperty(value = "下班时间")
    @JsonFormat(
            pattern = "YYYY-MM-dd HH:mm:ss"
    )
    private LocalDateTime endWorkTime;
}
