package com.lion.manage.entity.rule.vo;

import com.lion.manage.entity.enums.WashDeviceType;
import com.lion.manage.entity.rule.WashTemplateItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午11:36
 */
@Data
@ApiModel
public class ListWashTemplateItemVo extends WashTemplateItem {

    @ApiModelProperty(value = "洗手设备类型")
    List<WashDeviceType> washDeviceTypes;
}
