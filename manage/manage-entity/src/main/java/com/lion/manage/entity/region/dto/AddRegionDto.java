package com.lion.manage.entity.region.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.enums.ExposeObject;
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
@JsonIgnoreProperties(ignoreUnknown = true,value = {"id","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddRegionDto extends Region {

    @ApiModelProperty(value = "cctv id")
    private List<Long> cctvIds;

    @ApiModelProperty(value = "公开对象(STAFF(0, \"职员\"),POSTDOCS(1, \"流动人员\"),PATIENT(2, \"患者\"))-不要传中文就可以")
    private List<ExposeObject> exposeObjects;


}
