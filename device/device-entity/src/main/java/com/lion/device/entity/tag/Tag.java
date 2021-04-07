package com.lion.device.entity.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.enums.TagType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7上午9:04
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_tag" )
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "标签")
public class Tag extends BaseEntity {

    @ApiModelProperty(value = "标签分类")
    @Convert(converter = TagType.TagTypeConverter.class)
    @NotNull(message = "标签分类不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private TagType type;
}
