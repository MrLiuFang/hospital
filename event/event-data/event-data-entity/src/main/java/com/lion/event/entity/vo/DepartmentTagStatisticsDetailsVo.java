package com.lion.event.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lion.device.entity.tag.Tag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/22 上午10:44
 */
@Data
@ApiModel
public class DepartmentTagStatisticsDetailsVo {

    @ApiModelProperty(value = "标签总数")
    private Integer tagCount = 0;

    @ApiModelProperty(value = "正常标签总数")
    private Integer normalTagCount;

    @ApiModelProperty(value = "异常标签总数")
    private Integer abnormalTagCount;

    @ApiModelProperty(value = "部门信息")
    private List<DepartmentTagStatisticsDetailsVo.DepartmentVo> departmentVos;

    @ApiModel
    @Data
    public static class DepartmentVo{

        @ApiModelProperty(value = "科室id")
        private Long departmentId;

        @ApiModelProperty(value = "科室名称")
        private String departmentName;

        @ApiModelProperty(value = "标签信息")
        private List<DepartmentTagStatisticsDetailsVo.TagVo> tagVos;
    }

    @ApiModel
    @Data
    public static class TagVo extends Tag {

        @ApiModelProperty(value = "当前温度")
        private BigDecimal temperature;

        @ApiModelProperty(value = "当前湿度")
        private BigDecimal humidity;

        @ApiModelProperty(value = "数据记录时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime dataDateTime;
    }

}
