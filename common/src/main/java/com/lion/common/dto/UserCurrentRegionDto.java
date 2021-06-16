package com.lion.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/24 下午8:31
 **/
@Data
@ApiModel
public class UserCurrentRegionDto extends CurrentRegionDto implements Serializable {

    private static final long serialVersionUID = -2870941843121278585L;
    @ApiModelProperty(value = "上次所在的区域Id")
    private Long previousRegionId;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "当前区域事件次数(主要洗手事件数量记录)")
    private Integer currentRegionEvent = 0;

    @ApiModelProperty(value = "当前所在区域的洗手记录")
    private List<WashRecord> washRecordList;

    /**
     * 用于员工在该区域的后续的洗手事件关联(记录洗手事件如有违规,再记录该违规事件的洗手事件时间)
     */
    private String uuid;

    public void setWashRecord(WashRecord washRecord){
        if (Objects.isNull(washRecordList)) {
            washRecordList = new ArrayList<WashRecord>();
        }
        washRecordList.add(washRecord);
    }

    @Data
    @ApiModel
    public static class WashRecord implements Serializable{

        private static final long serialVersionUID = -640132126179527186L;
        @ApiModelProperty(value = "洗手发生的时间")
        private LocalDateTime dateTime;

        @ApiModelProperty(value = "洗手设备的id")
        private Long deviceId;
    }
}
