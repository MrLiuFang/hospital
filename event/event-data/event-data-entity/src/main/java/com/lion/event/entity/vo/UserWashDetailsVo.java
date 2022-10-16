package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema
public class UserWashDetailsVo extends User {

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "头像")
    private String headPortraitUrl;

    @Schema(description = "用户类型")
    private UserType userType;

    @Schema(description = "合规率")
    private BigDecimal conformance = new BigDecimal(0);

    @Schema(description = "洗手事件列表（不返回总行数）")
    private List<UserWashEvent> userWashEvent;

    @Data
    @Schema
    public static class UserWashEvent{

        @Schema(description = "使用设备")
        private String deviceName;

        @Schema(description = "所属区域")
        private String regionName;

        @Schema(description = "是否合规")
        private Boolean isConformance;

        @Schema(description = "洗手时长(秒)")
        private Integer time;

        @Schema(description = "设备分类")
        private DeviceClassify deviceClassify;

        @Schema(description = "设备分类")
        private DeviceType deviceType;

        @Schema(description = "cctv-可能多个逗号隔开")
        private String cctvUrl;

        @Schema(description = "时间")
        @JsonFormat(
                pattern = "YYYY-MM-dd HH:mm:ss"
        )
        private LocalDateTime dateTime;
    }
}
