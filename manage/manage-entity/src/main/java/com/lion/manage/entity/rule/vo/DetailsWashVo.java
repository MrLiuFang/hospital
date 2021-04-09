package com.lion.manage.entity.rule.vo;

import com.lion.core.persistence.Validator;
import com.lion.manage.entity.enums.WashDeviceType;
import com.lion.manage.entity.rule.Wash;
import com.lion.upms.entity.enums.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午5:12
 */
@Data
@ApiModel
public class DetailsWashVo extends Wash {

    @ApiModelProperty(value = "洗手设备类型")
    private List<WashDeviceType> deviceType;

    @ApiModelProperty(value = "区域(总数获取该数组大小)")
    private List<RegionVo> regionVos;

    @ApiModelProperty(value = "用户(总数获取该数组大小)")
    private List<UserVo> userVos;

    @Data
    @ApiModel
    public static class RegionVo{

        @ApiModelProperty(value = "id")
        private Long id;

        @ApiModelProperty(value = "区域名称")
        private String regionName;

        @ApiModelProperty(value = "建筑名称")
        private String buildName;

        @ApiModelProperty(value = "建筑楼层名称")
        private String buildFloorName;

        @ApiModelProperty(value = "备注")
        private String remarks;
    }

    @Data
    @ApiModel
    public static class UserVo{

        @ApiModelProperty(value = "id")
        private Long id;

        @ApiModelProperty(value = "姓名")
        private String name;

        @ApiModelProperty(value = "科室名称")
        private String departmentName;

        @ApiModelProperty(value = "员工类型")
        private UserType userType;

        @ApiModelProperty(value = "员工编号")
        private Integer number;

        @ApiModelProperty(value = "标签编码")
        private String tagCode;
    }

}

