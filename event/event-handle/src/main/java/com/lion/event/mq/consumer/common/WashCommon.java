package com.lion.event.mq.consumer.common;

import com.lion.common.dto.UserCurrentRegionDto;
import com.lion.common.dto.WashRecordDto;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/18 下午3:39
 **/
@Service
public class WashCommon {

    /**
     *
     * @param pi 员工id
     * @param ri 区域id
     * @param dvi 洗手设备id
     * @param ui uuid
     * @param ddt 设备产生时间
     * @param sdt 系统时间
     * @return
     */
    public WashRecordDto init(Long pi, Long ri, Long dvi,String ui, LocalDateTime ddt,LocalDateTime sdt){
        WashRecordDto dto = new WashRecordDto();
        dto.setPi(pi);
        dto.setRi(ri);
        dto.setDvi(dvi);
        dto.setUi(ui);
        dto.setDdt(ddt);
        dto.setSdt(sdt);
        return dto;
    }

}
