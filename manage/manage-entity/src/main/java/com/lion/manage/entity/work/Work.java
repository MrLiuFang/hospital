package com.lion.manage.entity.work;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/12 下午8:02
 **/
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_work",indexes = {@Index(columnList = "user_id")} )
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "员工上下班")
public class Work extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 6314578566875824981L;
    @ApiModelProperty(value = "员工id")
    @Column(name = "user_id")
    private Long userId;

    @ApiModelProperty(value = "上班时间")
    private LocalDateTime startWorkTime;

    @ApiModelProperty(value = "下班时间")
    private LocalDateTime endWorkTime;

}
