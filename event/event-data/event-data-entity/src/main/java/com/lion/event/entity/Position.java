package com.lion.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.common.dto.PositionDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //位置(人员,设备......)
 * @Date 2021/5/1 上午11:24
 **/
@Data
@Document(value = "position")
public class Position extends PositionDto implements Serializable {

    private static final long serialVersionUID = 2185500636981747571L;
}
