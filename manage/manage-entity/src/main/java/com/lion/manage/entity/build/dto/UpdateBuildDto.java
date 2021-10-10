package com.lion.manage.entity.build.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.build.Build;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午1:49
 */
@Data
@Schema
public class UpdateBuildDto extends Build {
}
