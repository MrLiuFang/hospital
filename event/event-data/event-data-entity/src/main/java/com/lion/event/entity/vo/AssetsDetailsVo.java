package com.lion.event.entity.vo;

import com.lion.core.persistence.Validator;
import com.lion.device.entity.fault.vo.FaultDetailsVo;
import com.lion.event.entity.Position;
import com.lion.manage.entity.assets.Assets;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/22 下午7:12
 */
@Data
@Schema
public class AssetsDetailsVo extends Assets {

//    @Schema(description = "行动轨迹（30天的数据）")
//    private List<Position> positions;

    @Schema(description = "使用登记(无数据-使用登记功能木有)")
    private List<Object> useRecord;

    @Schema(description = "区域名称")
    private String regionName;

    @Schema(description = "所属建筑")
    private String buildName;

    @Schema(description = "所属建筑楼层")
    private String buildFloorName;

    @Schema(description = "所属科室")
    private String departmentName;

    @Schema(description = "电量(0=正常,1=少於90 天,2=少於30天)")
    private Integer battery;

}
