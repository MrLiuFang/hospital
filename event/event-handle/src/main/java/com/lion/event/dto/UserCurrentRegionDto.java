package com.lion.event.dto;

import com.lion.manage.entity.region.Region;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/24 下午8:31
 **/
@Data
public class UserCurrentRegionDto implements Serializable {
    private static final long serialVersionUID = -90000050L;
    /**
     * 上次事件所在区域
     */
    private Long previousRegionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 当前所在的区域Id
     */
    private Long regionId;

    /**
     * 第一次进入时间
     */
    private LocalDateTime firstEntryTime;

    /**
     * 当前区域事件次数
     */
    private Integer currentRegionEvent = 0;

    /**
     * 当前所在区域的洗手记录
     */
    private List<WashRecord> washRecordList;

    public void setWashRecord(WashRecord washRecord){
        if (Objects.isNull(washRecordList)) {
            washRecordList = new ArrayList<WashRecord>();
        }
        washRecordList.add(washRecord);
    }

    @Data
    public static class WashRecord implements Serializable{
        private static final long serialVersionUID = -90000050L;
        /**
         * 洗手时间
         */
        private LocalDateTime dateTime;

        /**
         * 洗手设备的id
         */
        private Long deviceId;
    }
}