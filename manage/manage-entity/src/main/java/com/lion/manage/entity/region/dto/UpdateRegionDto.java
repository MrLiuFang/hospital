package com.lion.manage.entity.region.dto;

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
public class UpdateRegionDto extends Region {

    @Schema(description = "cctv id-全量先删后增")
    private List<Long> cctvIds;

//    @Schema(description = "公开对象(STAFF(0, \"职员\"),POSTDOCS(1, \"流动人员\"),PATIENT(2, \"患者\"))-不要传中文就可以")
//    private List<ExposeObject> exposeObjects;

    @Schema(description = "病房ID-全量先删后增")
    private List<Long> wardRoomIds;

    @Schema(description = "病床ID-全量先删后增")
    private List<Long> wardRoomSickbedIds;

    @Schema(description = "警示铃id-全量先删后增")
    private List<Long> warningBellIds;

    @Schema(description = "定位设备id-全量先删后增")
    private List<Long> deviceIds;

    @Schema(description = "綁定病房/病床(WARD/SICKBED)")
    private String bindType;
}
