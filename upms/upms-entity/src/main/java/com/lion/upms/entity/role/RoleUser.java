package com.lion.upms.entity.role;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author Mr.Liu
 * @Description: 角色与用户关联表
 * @date 2021/3/22下午3:22
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_role_user",indexes = {@Index(columnList = "role_id"),@Index(columnList = "user_id")})
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "角色与用户关联表")
public class RoleUser extends BaseEntity {

    @Column(name = "role_id",nullable = false)
    private Long roleId;

    @Column(name = "user_id",nullable = false)
    private Long userId;
}
