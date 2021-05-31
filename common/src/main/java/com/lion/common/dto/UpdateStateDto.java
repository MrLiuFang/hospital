package com.lion.common.dto;

import com.lion.common.enums.Type;
import com.lion.device.entity.enums.State;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/31 上午11:08
 */
@Data
public class UpdateStateDto implements Serializable {

    private Type type;

    private Long id;

    private Integer state;
}
