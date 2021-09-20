package com.lion.manage.entity.region.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.WarningBell;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionType;
import com.lion.manage.entity.rule.vo.DetailsWashTemplateVo;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
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

    @ApiModelProperty(value = "所有关联的设备")
    @JsonIgnoreProperties(ignoreUnknown = true,value = {"y","x","buildFloorId","buildId","electricity","warrantyPeriodDate","purchaseDate","warrantyPeriod","createDateTime","updateDateTime","createUserId","updateUserId"})
    private List<Device> devices;

    @ApiModelProperty(value = "所有cctv")
    @JsonIgnoreProperties(ignoreUnknown = true,value = {"regionId","buildFloorId","buildId","port","ip","createDateTime","updateDateTime","createUserId","updateUserId"})
    private List<Cctv> cctvs;

    @ApiModelProperty(value = "所有关联的病房")
    @JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
    private List<WardRoom> wardRooms;

    @ApiModelProperty(value = "所有关联的病床")
    @JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
    private List<WardRoomSickbed> wardRoomSickbeds;

    @ApiModelProperty(value = "区域类型")
    private RegionType regionType;

    @ApiModelProperty(value = "洗手规则模板")
    @JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
    private DetailsWashTemplateVo washTemplateVo;

    @ApiModelProperty(value = "警示。铃")
    @JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
    private List<WarningBell> warningBells;



//    @ApiModelProperty(value = "公开对象")
//    private List<ExposeObject> exposeObjects;
}
