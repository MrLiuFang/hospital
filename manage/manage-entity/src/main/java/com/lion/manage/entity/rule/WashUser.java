package com.lion.manage.entity.rule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午4:22
 */
@Entity
@Table(name = "t_wash_user",indexes = {@Index(columnList ="wash_id" ),@Index(columnList ="user_id" )})
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "洗手规则用户")
public class WashUser extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3307262103341434842L;
    @ApiModelProperty(value = "洗手规则id")
    @Column(name = "wash_id")
    @NotNull(message = "{2000049}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long washId;

    @ApiModelProperty(value = "用户id")
    @Column(name = "user_id")
    @NotNull(message = "{2000053}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long userId;
}
