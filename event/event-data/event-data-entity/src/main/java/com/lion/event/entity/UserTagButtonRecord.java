package com.lion.event.entity;

import com.lion.common.dto.UserTagButtonRecordDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/15 下午3:14
 */
@Data
@Document(value = "user_tag_button_record")
@ApiModel
public class UserTagButtonRecord extends UserTagButtonRecordDto {

    @Id
    private String _id;

    @ApiModelProperty(value = "员工姓名")
    private String n;
}
