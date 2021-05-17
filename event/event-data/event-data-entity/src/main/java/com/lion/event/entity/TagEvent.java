package com.lion.event.entity;

import com.lion.common.dto.TagEventDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/16 下午5:36
 **/
@Data
@Document(value = "tag_event")
public class TagEvent extends TagEventDto implements Serializable {
    @Id
    private String _id;

}
