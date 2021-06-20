package com.lion.event.entity.vo;

import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.cctv.vo.CctvVo;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.vo.DetailsDeviceVo;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.tag.Tag;
import com.lion.manage.entity.assets.vo.DetailsAssetsVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/20 下午2:17
 */
@Data
@ApiModel
public class DepartmentDeviceGroupStatisticsDetailsVo {

    @ApiModelProperty(value = "设备组总数")
    private Integer deviceGroupCount = 0;

    @ApiModelProperty(value = "正常设备总数")
    private Integer normalDeviceCount = 0;

    @ApiModelProperty(value = "异常设备总数")
    private Integer abnormalDeviceCount= 0;

    @ApiModelProperty(value = "设备组")
    private List<DeviceGroupDetailsVo> deviceGroupDetailsVos;

    @Data
    @ApiModel
    public static class DeviceGroupDetailsVo {

        @ApiModelProperty(value = "设备组名称")
        private String name;

        @ApiModelProperty(value = "设备")
        private List<DetailsDeviceVo> detailsDeviceVos;
    }
}
