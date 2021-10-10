package com.lion.device.entity.device;

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
 * @date 2021/3/31上午11:19
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_device_group")

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@Schema(description = "设备组")
public class DeviceGroup extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 2183390610432287366L;
    @Schema(description = "设备组名称")
    @Column(name = "name")
    @NotBlank(message = "{4000008}", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @Schema(description = "设备组编号")
    @Column(name = "code")
    @NotBlank(message = "{4000009}", groups = {Validator.Insert.class, Validator.Update.class})
    private String code;

    @Schema(description = "备注")
    @Column(name = "remarks")
    private String remarks;
}
