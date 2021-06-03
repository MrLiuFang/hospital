package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.assets.Assets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/21 下午9:38
 */
@Data
@ApiModel
public class DepartmentAssetsStatisticsDetailsVo {

    @ApiModelProperty(value = "资产总数")
    private Integer assetsCount = 0;

    @ApiModelProperty(value = "正常资产总数")
    private Integer normalAssetsCount= 0;

    @ApiModelProperty(value = "异常资产总数")
    private Integer abnormalAssetsCount= 0;

    @ApiModelProperty(value = "部门信息")
    private List<DepartmentAssetsStatisticsDetailsVo.DepartmentVo> departmentVos;

    @ApiModel
    @Data
    public static class DepartmentVo{

        @ApiModelProperty(value = "科室id")
        private Long departmentId;

        @ApiModelProperty(value = "科室名称")
        private String departmentName;

        @ApiModelProperty(value = "资产信息")
        private List<AssetsVo> assetsVos;
    }

    @ApiModel
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true,value = {"img","departmentId","buildFloorId","buildId","regionId","createDateTime","updateDateTime","createUserId","updateUserId"})
    public static class AssetsVo extends Assets{

        @ApiModelProperty(value = "标签电量")
        private Integer battery;
    }
}
