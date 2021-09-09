package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.upms.entity.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/12 下午5:40
 **/
@Data
@ApiModel
public class UserWashDetailsVo extends User {

    @ApiModelProperty(value = "科室名称")
    private String departmentName;

    @ApiModelProperty(value = "头像")
    private String headPortraitUrl;

    @ApiModelProperty(value = "合规率")
    private BigDecimal conformance = new BigDecimal(0);

    @ApiModelProperty(value = "洗手事件列表（不返回总行数）")
    private List<UserWashEvent> userWashEvent;

    @Data
    @ApiModel
    public static class UserWashEvent{

        @ApiModelProperty(value = "使用设备")
        private String deviceName;

        @ApiModelProperty(value = "所属区域")
        private String regionName;

        @ApiModelProperty(value = "是否合规")
        private Boolean isConformance;

        @ApiModelProperty(value = "洗手时长(秒)")
        private Integer time;

        @ApiModelProperty(value = "时间")
        @JsonFormat(
                pattern = "YYYY-MM-dd HH:mm:ss"
        )
        private LocalDateTime dateTime;
    }
}
