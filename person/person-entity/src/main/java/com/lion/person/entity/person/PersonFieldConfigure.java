package com.lion.person.entity.person;

import com.lion.core.persistence.entity.BaseEntity;
import com.lion.person.entity.enums.ConfigureType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/23 上午10:30
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_person_field_configure")
@DynamicInsert
@Data
@Schema(description = "患者自定义字段")
public class PersonFieldConfigure extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3693905501287233798L;

    @Column(name = "content" ,length = 2000)
    private String content;


    private ConfigureType configureType;
}
