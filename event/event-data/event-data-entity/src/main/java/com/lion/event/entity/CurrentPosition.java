package com.lion.event.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author Mr.Liu
 * @Description //当前位置
 * @Date 2021/5/15 下午2:59
 **/
@Data
@Document(value = "current_position")
public class CurrentPosition extends Position {
}
