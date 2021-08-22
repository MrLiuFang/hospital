package com.lion.event.entity.vo;

import com.lion.core.persistence.Validator;
import com.lion.device.entity.fault.vo.FaultDetailsVo;
import com.lion.event.entity.Position;
import com.lion.manage.entity.assets.Assets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel
public class AssetsDetailsVo extends Assets {

//    @ApiModelProperty(value = "行动轨迹（30天的数据）")
//    private List<Position> positions;

    @ApiModelProperty(value = "使用登记(无数据-使用登记功能木有)")
    private List<Object> useRecord;

    @ApiModelProperty(value = "区域名称")
    private String regionName;

    @ApiModelProperty(value = "所属建筑")
    private String buildName;

    @ApiModelProperty(value = "所属建筑楼层")
    private String buildFloorName;

    @ApiModelProperty(value = "所属科室")
    private String departmentName;

    @ApiModelProperty(value = "电量(0=正常,1=少於90 天,2=少於30天)")
    private Integer battery;

}
