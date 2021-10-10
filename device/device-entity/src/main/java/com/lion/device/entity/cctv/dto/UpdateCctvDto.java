package com.lion.device.entity.cctv.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.cctv.Cctv;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:39
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"deviceState","lastDataTime","createDateTime","updateDateTime","createUserId","updateUserId"})
public class UpdateCctvDto extends Cctv {
}
