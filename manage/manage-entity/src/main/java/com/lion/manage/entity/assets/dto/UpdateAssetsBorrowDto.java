package com.lion.manage.entity.assets.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.manage.entity.assets.AssetsBorrow;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午8:37
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true,value = {"borrowTime","borrowUserId","returnTime","createDateTime","updateDateTime","createUserId","updateUserId"})
public class UpdateAssetsBorrowDto extends AssetsBorrow {
}
