package com.lion.manage.entity.assets.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.assets.AssetsFaultReport;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2021-08-09 10:19
 **/
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
public class ListAssetsFaultReportVo extends AssetsFaultReport {
}
