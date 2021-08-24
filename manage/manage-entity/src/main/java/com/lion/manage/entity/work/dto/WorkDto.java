package com.lion.manage.entity.work.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 上午10:30
 */
@Data
@ApiModel
public class WorkDto implements Serializable {

    private static final long serialVersionUID = -686488803847327337L;
    @ApiModelProperty(value = "员工id")
    @NotNull(message = "{2000058}")
    private Long userId;
}
