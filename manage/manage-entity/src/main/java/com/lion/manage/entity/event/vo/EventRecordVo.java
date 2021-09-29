package com.lion.manage.entity.event.vo;

import com.lion.manage.entity.event.EventRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/29 上午10:08
 */
@Data
@ApiModel
public class EventRecordVo extends EventRecord {

    @ApiModelProperty(value = "创建人姓名")
    private String createUserName;

    @ApiModelProperty(value = "创建人头像")
    private Long headPortrait;

    @ApiModelProperty(value = "创建人头像url")
    private String headPortraitUrl;
}
