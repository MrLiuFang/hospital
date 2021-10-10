package com.lion.manage.entity.department;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Mr.Liu
 * @Description: 科室用户关联
 * @date 2021/3/23下午2:05
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_department_user"
        ,indexes = {@Index(columnList = "department_id"),@Index(columnList = "user_id")})

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@Schema(description = "科室用户关联")
public class DepartmentUser extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3932412863865447235L;
    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "user_id")
    private Long userId;
}
