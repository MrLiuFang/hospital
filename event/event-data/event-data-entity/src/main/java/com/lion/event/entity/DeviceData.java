package com.lion.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //事件原始数据
 * @Date 2021/4/23 上午9:45
 **/
@Data
@Document(value = "device_data")
@ApiModel
public class DeviceData implements Serializable {

    private static final long serialVersionUID = -2540094505107531820L;
    @Id
    private String _id;

    @ApiModelProperty(value = "tag id")
    private Long ti;

    @ApiModelProperty(value = "tag code")
    private String tc;

    @ApiModelProperty(value = "tag name")
    private String tn;

    @ApiModelProperty(value = "monitor id")
    private Long mi;

    @ApiModelProperty(value = "monitor code")
    private String mc;

    @ApiModelProperty(value = "monitor name")
    private String mn;

    @ApiModelProperty(value = "star id")
    private Long si;

    @ApiModelProperty(value = "star code")
    private String sc;

    @ApiModelProperty(value = "star name")
    private String sn;

    @ApiModelProperty(value = "事件")
    private String e;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "设备产生時間")
    private LocalDateTime ddt;

    @ApiModelProperty(value = "系统接受到时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sdt;
}
