package com.lion.event.entity.vo;

import com.lion.event.entity.UserTagButtonRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/17 下午3:01
 */
@Data
@ApiModel
public class ListUserTagButtonRecordVo extends UserTagButtonRecord {

    @ApiModelProperty(value = "头像（文件id）")
    private Long headPortrait;

    @ApiModelProperty(value = "头像")
    private String headPortraitUrl;
}
