package com.lion.event.entity;

import com.lion.common.dto.UserTagButtonRecordDto;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/15 下午3:14
 */
@Data
@Document(value = "user_tag_button_record")
@Schema
public class UserTagButtonRecord extends UserTagButtonRecordDto implements Serializable {

    private static final long serialVersionUID = -8445564254600194613L;
    @Id
    private String _id;

    @Schema(description = "员工姓名")
    private String n;
}
