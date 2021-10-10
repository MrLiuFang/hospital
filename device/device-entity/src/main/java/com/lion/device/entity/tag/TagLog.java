package com.lion.device.entity.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.enums.TagLogContent;
import com.lion.device.entity.enums.TagState;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 下午4:40
 **/
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_tag_log" ,indexes = {@Index(columnList = "tag_id"),@Index(columnList = "user_id")})

@DynamicInsert
@Data
@Schema(description = "标签日志")
public class TagLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 8039008614074904670L;
    @Schema(description = "标签id")
    @Column(name = "tag_id")
    private Long tagId;

    @Schema(description = "操作人id")
    @Column(name = "user_id")
    private Long userId;

    @Schema(description = "操作内容")
    @Column(name = "content")
    @Convert(converter = TagLogContent.TagLogContentConverter.class)
    private TagLogContent content;
}
