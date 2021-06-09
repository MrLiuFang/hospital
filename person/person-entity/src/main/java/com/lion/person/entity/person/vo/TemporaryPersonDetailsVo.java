package com.lion.person.entity.person.vo;

import com.lion.person.entity.person.TemporaryPerson;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午4:10
 */
@Data
@ApiModel
public class TemporaryPersonDetailsVo extends TemporaryPerson {

    @ApiModelProperty(value = "头像")
    private String headPortraitUrl;

    @ApiModelProperty(value = "电量(0=正常,1=少於90 天,2=少於30天)")
    private Integer battery;

    @ApiModelProperty(value = "科室名称（来源于拜访人）")
    private String departmentName;

    @ApiModelProperty(value = "限制区域")
    private List<TemporaryPersonDetailsVo.RestrictedAreaVo> restrictedAreaVoList;

    @Data
    @ApiModel
    public static class RestrictedAreaVo{

        @ApiModelProperty(value = "区域id")
        private Long regionId;

        @ApiModelProperty(value = "区域名称")
        private String regionName;

        @ApiModelProperty(value = "建筑名称")
        private String buildName;

        @ApiModelProperty(value = "建筑楼成名称")
        private String buildFloorName;

        @ApiModelProperty(value = "备注")
        private String remark;
    }


}
