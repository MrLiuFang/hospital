package com.lion.event.entity;

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
public class TagEvent extends TagRecord implements Serializable {
    @Id
    private String _id;

    //类型 (com.lion.event.entity.enums.Type)
    private Integer typ;

    //最高温度
    private BigDecimal mxt;

    //最低温度
    private BigDecimal mit;

    //最高湿度
    private BigDecimal mxh;

    //最低湿度
    private BigDecimal mih;

}
