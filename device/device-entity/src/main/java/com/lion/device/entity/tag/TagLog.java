package com.lion.device.entity.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.TagLogContent;
import com.lion.device.entity.enums.TagState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 下午4:40
 **/
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_tag_log" ,indexes = {@Index(columnList = "tag_id"),@Index(columnList = "user_id")})
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "标签日志")
public class TagLog extends BaseEntity {

    @ApiModelProperty(value = "标签id")
    @Column(name = "tag_id",nullable = false)
    private Long tagId;

    @ApiModelProperty(value = "操作人id")
    @Column(name = "user_id",nullable = false)
    private Long userId;

    @ApiModelProperty(value = "操作内容")
    @Column(name = "content")
    @Convert(converter = TagLogContent.TagLogContentConverter.class)
    private TagLogContent content;
}
