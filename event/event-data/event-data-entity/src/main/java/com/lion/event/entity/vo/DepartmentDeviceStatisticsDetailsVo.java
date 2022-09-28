package com.lion.event.entity.vo;

import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.cctv.vo.CctvVo;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.vo.DetailsDeviceVo;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.tag.Tag;
import com.lion.manage.entity.assets.vo.DetailsAssetsVo;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/20 下午2:17
 */
@Data
@Schema
public class DepartmentDeviceStatisticsDetailsVo {

//    @Schema(description = "设备组总数")
//    private Integer deviceGroupCount = 0;

    @Schema(description = "正常设备总数")
    private Integer normalDeviceCount = 0;

    @Schema(description = "异常设备总数")
    private Integer abnormalDeviceCount= 0;

//    @Schema(description = "设备组")
//    private List<DeviceGroupDetailsVo> deviceGroupDetailsVos;

    @Schema(description = "科室")
    private List<DepartmentDeviceDetailsVo> departmentDeviceDetailsVos;

//    @Data
//    @Schema
//    public static class DeviceGroupDetailsVo {
//
//        @Schema(description = "设备组名称")
//        private String name;
//
//        @Schema(description = "设备")
//        private List<DetailsDeviceVo> detailsDeviceVos;
//    }

    @Data
    @Schema
    public static class DepartmentDeviceDetailsVo {

        @Schema(name = "科室名称")
        private String departmentName;

        @Schema(name = "科室ID")
        private Long departmentId;

        @Schema(name = "设备")
        private List<DetailsDeviceVo> detailsDeviceVos;

        @Schema(name = "cctv")
        private List<Cctv> cctvs;
    }
}
