package com.lion.manage.entity.assets.vo;

import com.lion.manage.entity.assets.Assets;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午7:44
 */
@Data
@Schema
public class ListAssetsVo extends Assets {

    @Schema(description = "位置")
    private String position;

    @Schema(description = "科室名称")
    private String departmentName;

    @Schema(description = "标签码")
    private String tagCode;
}
