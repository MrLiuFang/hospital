package com.lion.manage.entity.ward;

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

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@Schema(description = "病房(房间)")
public class WardRoom extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 8001669825400228305L;
    @Schema(description = "病房id")
    @Column(name = "ward_id")
    @NotNull(message = "{2000055}")
    private Long wardId;

    @Schema(description = "房间编号")
    @Column(name = "code")
    @NotBlank(message = "{2000056}", groups = {Validator.Insert.class, Validator.Update.class})
    private String code;

    @Schema(description = "区域id")
    @Column(name = "region_id")
    private Long regionId;
}
