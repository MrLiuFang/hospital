package com.lion.device.entity.tag;

import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.TagRuleLogType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午10:49
 **/

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_tag_rule_log" ,indexes = {@Index(columnList = "tag_rule_id")})

@DynamicInsert
@Data
@Schema(description = "标签规则日志")
public class TagRuleLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 6763479803334545228L;
    @Schema(description = "标签id")
    @Column(name = "tag_rule_id")
    private Long tagRuleId;

    @Schema(description = "操作人")
    @Column(name = "user_id")
    private Long userId;

    @Schema(description = "操作内容")
    @Column(name = "content")
    private String content;

    @Schema(description = "日志类型")
    @Column(name = "action_type")
    @Convert(converter = TagRuleLogType.TagRuleTypeConverter.class)
    private TagRuleLogType actionType;
}
