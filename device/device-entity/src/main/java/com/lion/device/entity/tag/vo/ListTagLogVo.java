package com.lion.device.entity.tag.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.device.entity.enums.TagLogContent;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 下午4:52
 **/
@Data
@Schema
public class ListTagLogVo {

    @Schema(description = "操作时间")
    @JsonFormat(
            pattern = "YYYY-MM-dd HH:mm:ss"
    )
    private LocalDateTime dateTime;

    @Schema(description = "用户姓名")
    private String name;

    @Schema(description = "用户编号")
    private Integer number;

    @Schema(description = "操作内容")
    private TagLogContent content;

    @Schema(description = "头像（文件id）")
    private Long headPortrait;

    @Schema(description = "头像")
    private String headPortraitUrl;
}
