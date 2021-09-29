package com.lion.manage.entity.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/29 上午9:42
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_event_record" )
@DynamicInsert
@Data
@ApiModel(description = "事件记录")
public class EventRecord extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "事件编号")
    private String code;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "搜索条件-(Json)")
    private String searchCriteria;

    public EventRecord() {

    }
}
