package com.lion.manage.entity.department;

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
 * @Description:
 * @date 2021/3/23下午8:13
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_department_responsible_user"
        ,indexes = {@Index(columnList = "department_id"),@Index(columnList = "user_id")})
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "科室用户关联")
public class DepartmentResponsibleUser extends BaseEntity {

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "user_id")
    private Long userId;
}
