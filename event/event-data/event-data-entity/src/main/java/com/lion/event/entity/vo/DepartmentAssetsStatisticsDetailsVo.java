package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.enums.State;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.manage.entity.assets.Assets;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/21 下午9:38
 */
@Data
@Schema
public class DepartmentAssetsStatisticsDetailsVo {

    @Schema(description = "资产总数")
    private Integer assetsCount = 0;

    @Schema(description = "正常资产总数")
    private Integer normalAssetsCount= 0;

    @Schema(description = "异常资产总数")
    private Integer abnormalAssetsCount= 0;

    @Schema(description = "部门信息")
    private List<AssetsDepartmentVo> assetsDepartmentVos;

    @Schema
    @Data
    public static class AssetsDepartmentVo {

        @Schema(description = "科室id")
        private Long departmentId;

        @Schema(description = "科室名称")
        private String departmentName;

        @Schema(description = "资产信息")
        private List<AssetsVo> assetsVos;
    }

    @Schema
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true,value = {"img","departmentId","buildFloorId","buildId","regionId","createDateTime","updateDateTime","createUserId","updateUserId"})
    public static class AssetsVo extends Assets{

        @Schema(description = "标签类型")
        private TagType tagType;

        @Schema(description = "标签用途")
        private TagPurpose tagPurpose;

        @Schema(description = "标签编码")
        private String tagCode;

        @Schema(description = "设备名称（标签字段）")
        private String deviceName;

        @Schema(description = "设备编码（标签字段）")
        private String deviceCode;

        @Schema(description = "标签电量")
        private Integer battery;

        @Schema(description = "是否有未处理的故障")
        private Boolean isFault;
    }
}
