package com.lion.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/24 下午8:31
 **/
@Data
@ApiModel
public class UserCurrentRegionDto implements Serializable {
    private static final long serialVersionUID = -90000050L;

    @ApiModelProperty(value = "上次所在的区域Id")
    private Long previousRegionId;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "当前所在的区域Id")
    private Long regionId;

    @ApiModelProperty(value = "第一次进入时间")
    private LocalDateTime firstEntryTime;

    @ApiModelProperty(value = "当前区域事件次数")
    private Integer currentRegionEvent = 0;

    @ApiModelProperty(value = "当前所在区域的洗手记录")
    private List<WashRecord> washRecordList;

    public void setWashRecord(WashRecord washRecord){
        if (Objects.isNull(washRecordList)) {
            washRecordList = new ArrayList<WashRecord>();
        }
        washRecordList.add(washRecord);
    }

    @Data
    @ApiModel
    public static class WashRecord implements Serializable{
        private static final long serialVersionUID = -90000050L;

        @ApiModelProperty(value = "洗手发生的时间")
        private LocalDateTime dateTime;

        @ApiModelProperty(value = "洗手设备的id")
        private Long deviceId;
    }
}
