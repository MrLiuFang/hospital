package com.lion.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午3:52
 **/
@Data
public class TagCurrentRegionDto extends CurrentRegionDto implements Serializable {

    private Long tagId;
}
