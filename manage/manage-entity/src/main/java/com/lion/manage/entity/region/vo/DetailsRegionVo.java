package com.lion.manage.entity.region.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.DeviceGroup;
import com.lion.manage.entity.enums.ExposeObject;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionExposeObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午7:34
 */
@Data
@ApiModel
public class DetailsRegionVo extends Region {

    @ApiModelProperty(value = "所有设备组的设备")
    @JsonIgnoreProperties(ignoreUnknown = true,value = {"y","x","buildFloorId","buildId","electricity","warrantyPeriodDate","purchaseDate","warrantyPeriod","createDateTime","updateDateTime","createUserId","updateUserId"})
    private List<Device> devices;

    @ApiModelProperty(value = "所有cctv")
    @JsonIgnoreProperties(ignoreUnknown = true,value = {"regionId","buildFloorId","buildId","port","ip","createDateTime","updateDateTime","createUserId","updateUserId"})
    private List<Cctv> cctvs;

    @ApiModelProperty(value = "公开对象")
    private List<ExposeObject> exposeObjects;
}
