package com.lion.device.entity.tag.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.tag.Tag;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午8:21
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true,value = {"id","deviceState","lastDataTime","electricity","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddTagDto extends Tag {
}
