package com.lion.manage.entity.assets.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.assets.AssetsFault;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午8:43
 */
@Data
@Schema
@JsonIgnoreProperties(ignoreUnknown = true,value = {"finishTime","declarantUserId","declarantTime","describe","createDateTime","updateDateTime","createUserId","updateUserId"})
public class UpdateAssetsFaultDto extends AssetsFault {
}
