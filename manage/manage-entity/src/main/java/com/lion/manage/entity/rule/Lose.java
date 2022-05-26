package com.lion.manage.entity.rule;

import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_lose_alarm",indexes = {})
@DynamicInsert
@Data
@Schema(description = "失去联告警规则")
public class Lose extends BaseEntity implements Serializable {

    @Schema(description = "分钟")
    private Integer minute;

    @Schema(description = "患者级别")
    private Integer level;
}
