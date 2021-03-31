package com.lion.manage.entity.region;

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
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31上午10:11
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_build" ,indexes = {@Index(columnList = "name")})
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "区域")
public class Region extends BaseEntity {


}
