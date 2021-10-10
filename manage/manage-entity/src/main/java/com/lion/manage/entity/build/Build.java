package com.lion.manage.entity.build;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
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
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31上午9:58
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_build" )

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@Schema(description = "建筑")
public class Build extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3137908609554075165L;
    @Schema(description = "建筑名称")
    @Column(name = "name")
    @NotBlank(message = "{2000021}", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @Schema(description = "备注")
    @Column(name = "remarks")
    private String remarks;
}
