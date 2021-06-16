package com.lion.event.entity;

import com.lion.common.dto.WashEventDto;
import com.lion.common.dto.WashRecordDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午2:18
 **/
@Data
@Document(value = "wash_event")
public class WashEvent extends WashEventDto implements Serializable {

    private static final long serialVersionUID = 2189349216798656780L;
}
