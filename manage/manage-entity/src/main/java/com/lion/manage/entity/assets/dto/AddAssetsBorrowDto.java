package com.lion.manage.entity.assets.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.manage.entity.assets.AssetsBorrow;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午8:37
 */
@Data
@ApiModel
public class AddAssetsBorrowDto {

    @ApiModelProperty(value = "借用资产id")
    @Size(min = 1,message = "请选择要借用的资产")
    private List<Long> assestsIds;

    @ApiModelProperty(value = "借用科室Id")
    @NotNull(message = "资产id不能为空")
    private Long borrowDepartmentId;

    @ApiModelProperty(value = "借用床位Id")
    @NotNull(message = "借用床位id不能为空")
    private Long borrowWardRoomSickbedId;

    @ApiModelProperty(value = "借用开始时间(yyyy-MM-dd HH:mm:ss)")
    @NotNull(message = "借用开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;

    @ApiModelProperty(value = "借用结束时间(yyyy-MM-dd HH:mm:ss)")
    @NotNull(message = "借用结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    @ApiModelProperty(value = "借用人编号")
    @NotNull(message = "借用人编号不能为空")
    private Integer borrowUserNumber;

}
