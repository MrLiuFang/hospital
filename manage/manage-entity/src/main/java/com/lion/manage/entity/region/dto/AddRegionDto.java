package com.lion.manage.entity.region.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.region.Region;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午7:21
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"id","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddRegionDto extends Region {

    @Schema(description = "cctv id")
    private List<Long> cctvIds;

//    @Schema(description = "公开对象(STAFF(0, \"职员\"),POSTDOCS(1, \"流动人员\"),PATIENT(2, \"患者\"))-不要传中文就可以")
//    private List<ExposeObject> exposeObjects;

    @Schema(description = "病房ID")
    public List<Long> wardRoomIds;

    @Schema(description = "病床ID")
    public List<Long> wardRoomSickbedIds;

    @Schema(description = "警示铃id")
    public List<Long> warningBellIds;

    @Schema(description = "定位设备id")
    public List<Long> deviceIds;



}
