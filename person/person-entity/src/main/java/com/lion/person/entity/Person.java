package com.lion.person.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 下午2:57
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_person")
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "person")
public class Person extends BaseEntity {



}
