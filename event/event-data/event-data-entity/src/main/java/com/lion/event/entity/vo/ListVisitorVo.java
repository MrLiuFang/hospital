package com.lion.event.entity.vo;

import com.lion.event.entity.Position;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/10/18 上午10:41
 */
@Data
@Schema
public class ListVisitorVo extends Position {

    @Schema(description = "职员/病人/流动人员/资产等名称")
    private String name;

    @Schema(description = "职员/病人/流动人员/资产等图片id")
    private Long img;

    @Schema(description = "职员/病人/流动人员/资产等图片url")
    private String imgUrl;

    @Schema(description = "tagCode")
    private String tagCode;
}
