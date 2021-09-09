package com.lion.manage.entity.region.dto;

import com.lion.manage.entity.region.Region;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午7:21
 */
@Data
@ApiModel
public class UpdateRegionDto extends Region {

    @ApiModelProperty(value = "cctv id")
    private List<Long> cctvIds;

//    @ApiModelProperty(value = "公开对象(STAFF(0, \"职员\"),POSTDOCS(1, \"流动人员\"),PATIENT(2, \"患者\"))-不要传中文就可以")
//    private List<ExposeObject> exposeObjects;

    @ApiModelProperty(value = "病房ID")
    public List<Long> wardRoomIds;

    @ApiModelProperty(value = "病床ID")
    public List<Long> wardRoomSickbedIds;

    @ApiModelProperty(value = "警示铃id")
    public List<Long> warningBellIds;

    @ApiModelProperty(value = "定位设备id")
    public List<Long> deviceIds;
}
