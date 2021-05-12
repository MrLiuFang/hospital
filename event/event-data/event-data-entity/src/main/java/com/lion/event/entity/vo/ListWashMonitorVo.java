package com.lion.event.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //手卫生监控
 * @Date 2021/5/11 下午4:43
 **/
@Data
@ApiModel
public class ListWashMonitorVo {

    @ApiModelProperty(value = "科室合规率")
    private List<Ratio> department;

    @ApiModelProperty(value = "全院合规率")
    private Ratio hospital;

    @Data
    @ApiModel
    public static class Ratio {

        @ApiModelProperty(value = "全院/科室名称")
        private String name;

        @ApiModelProperty(value = "合规率")
        private BigDecimal conformance = new BigDecimal(0);

        @ApiModelProperty(value = "违规")
        private BigDecimal violation = new BigDecimal(0);

        @ApiModelProperty(value = "错过洗手")
        private BigDecimal noWash =new BigDecimal(0);
    }
}
