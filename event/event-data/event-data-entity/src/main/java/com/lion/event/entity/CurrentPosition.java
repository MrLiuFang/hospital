package com.lion.event.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Author Mr.Liu
 * @Description //当前位置
 * @Date 2021/5/15 下午2:59
 **/
@Data
@Document(value = "current_position")
@ApiModel
public class CurrentPosition extends Position implements Serializable {

    private static final long serialVersionUID = 1210769701993214844L;
}
