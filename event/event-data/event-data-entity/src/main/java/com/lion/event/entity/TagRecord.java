package com.lion.event.entity;

import com.lion.common.dto.TagRecordDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/16 下午5:36
 **/
@Data
@Document(value = "tag_record")
public class TagRecord extends TagRecordDto implements Serializable {

    @Id
    private String _id;

}
