package com.lion.manage.entity.ward;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午9:50
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_ward_room",indexes = {@Index(columnList = "code"),@Index(columnList = "ward_id")})
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "病房(房间)")
public class WardRoom extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 8001669825400228305L;
    @ApiModelProperty(value = "病房id")
    @Column(name = "ward_id")
    @NotNull(message = "{2000055}")
    private Long wardId;

    @ApiModelProperty(value = "房间编号")
    @Column(name = "code")
    @NotBlank(message = "{2000056}", groups = {Validator.Insert.class, Validator.Update.class})
    private String code;

//    @ApiModelProperty(value = "区域id")
//    @Column(name = "region_id")
//    @NotNull(message = "区域不能为空", groups = {Validator.Insert.class, Validator.Update.class})
//    private Long regionId;
}
