package com.lion.device.entity.device.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.device.WarningBell;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 下午2:03
 */
@Data
@ApiModel
public class UpdateWarningBellDto extends WarningBell {
}
