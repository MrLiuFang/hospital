package com.lion.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午4:01
 **/
@Data
public class AssetsCurrentRegionDto extends CurrentRegionDto implements Serializable {

    private Long assetsId;
}
