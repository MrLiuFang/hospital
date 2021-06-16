package com.lion.event.entity;

import com.lion.common.dto.RecyclingBoxRecordDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.swing.plaf.basic.BasicOptionPaneUI;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午10:45
 */
@Data
@Document(value = "recycling_box_record")
public class RecyclingBoxRecord extends RecyclingBoxRecordDto {

    @Id
    private String _id;

    @ApiModelProperty(value = "是否消毒")
    private Boolean id = false;
}
