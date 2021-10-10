package com.lion.device.entity.tag.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.tag.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午8:29
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"deviceState","lastDataTime","electricity","createDateTime","updateDateTime","createUserId","updateUserId"})
public class UpdateTagDto extends Tag {
}
