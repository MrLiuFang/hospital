package com.lion.device.entity.tag;

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
 * @Date 2021/5/4 上午10:12
 **/
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_tag_rule_user" ,indexes = {@Index(columnList = "tag_rule_id"),@Index(columnList = "user_id")})
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "标签规则关联的用户")
public class TagRuleUser extends BaseEntity {

    @ApiModelProperty(value = "标签id")
    @NotNull(message = "标签id不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "tag_rule_id",nullable = false)
    private Long tagRuleId;

    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "user_id",nullable = false)
    private Long userId;
}