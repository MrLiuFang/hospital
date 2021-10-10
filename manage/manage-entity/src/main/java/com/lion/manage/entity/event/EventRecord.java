package com.lion.manage.entity.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "事件记录")
@JsonIgnoreProperties(
        ignoreUnknown = true,
        value = {"updateDateTime", "createUserId", "updateUserId"}
)
public class EventRecord extends BaseEntity implements Serializable {

    @Schema(description = "事件编号")
    private String code;

    @Schema(description = "备注")
    private String remarks;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "搜索条件-(Json)")
    private String searchCriteria;

    public EventRecord() {

    }
}
