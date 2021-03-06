package com.lion.manage.entity.assets.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.assets.AssetsFault;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午8:39
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"id","declarantTime","finishTime","finishUserId","createDateTime","updateDateTime","createUserId","updateUserId"})
public class AddAssetsFaultDto extends AssetsFault {
}
