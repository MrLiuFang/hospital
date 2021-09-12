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
 * @date 2021/4/1上午9:51
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_ward_room_sickbed",indexes = {@Index(columnList = "ward_room_id")})

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "病房(房间-病床)")
public class WardRoomSickbed extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 2539027088777851254L;
    @ApiModelProperty(value = "病房id")
    @Column(name = "ward_room_id")
    @NotNull(message = "{2000055}")
    private Long wardRoomId;

    @ApiModelProperty(value = "床位")
    @Column(name = "bed_code")
    @NotBlank(message = "{2000057}", groups = {Validator.Insert.class, Validator.Update.class})
    private String bedCode;

    @ApiModelProperty(value = "区域id")
    @Column(name = "region_id")
    private Long regionId;
}
