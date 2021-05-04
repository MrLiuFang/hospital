package com.lion.device.entity.tag;

import cn.hutool.cache.GlobalPruneTimer;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 上午10:49
 **/

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_tag_rule_log" ,indexes = {@Index(columnList = "tag_rule_id")})
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "标签规则日志")
public class TagRuleLog extends BaseEntity {

    @ApiModelProperty(value = "标签id")
    @Column(name = "tag_rule_id",nullable = false)
    private Long tagRuleId;

    @ApiModelProperty(value = "操作人")
    @Column(name = "user_id",nullable = false)
    private Long userId;

    @ApiModelProperty(value = "操作内容")
    @Column(name = "content",nullable = false)
    private String content;
}
