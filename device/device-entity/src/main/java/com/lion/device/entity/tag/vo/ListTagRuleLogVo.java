package com.lion.device.entity.tag.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.device.entity.enums.TagRuleLogType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 下午3:46
 **/
@Data
@Schema
public class ListTagRuleLogVo  {

    @Schema(description = "操作时间")
    @JsonFormat(
            pattern = "YYYY-MM-dd HH:mm:ss"
    )
    private LocalDateTime dateTime;

    @Schema(description = "用户姓名")
    private String name;

    @Schema(description = "操作内容")
    private String content;

    @Schema(description = "头像（文件id）")
    private Long headPortrait;

    @Schema(description = "头像")
    private String headPortraitUrl;

    @Schema(description = "日志类型")
    private TagRuleLogType actionType;
}
