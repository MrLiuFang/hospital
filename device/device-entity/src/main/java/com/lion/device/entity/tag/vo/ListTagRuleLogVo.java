package com.lion.device.entity.tag.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.device.entity.enums.TagRuleLogType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 下午3:46
 **/
@Data
@ApiModel
public class ListTagRuleLogVo  {

    @ApiModelProperty(value = "操作时间")
    @JsonFormat(
            pattern = "YYYY-MM-dd HH:mm:ss"
    )
    private LocalDateTime dateTime;

    @ApiModelProperty(value = "用户姓名")
    private String name;

    @ApiModelProperty(value = "操作内容")
    private String content;

    @ApiModelProperty(value = "头像（文件id）")
    private Long headPortrait;

    @ApiModelProperty(value = "头像")
    private String headPortraitUrl;

    @ApiModelProperty(value = "日志类型")
    private TagRuleLogType actionType;
}
