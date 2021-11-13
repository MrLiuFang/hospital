package com.lion.manage.entity.template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_search_template")
@DynamicInsert
@Data
@JsonIgnoreProperties()
@Schema(description = "搜索模板")
public class SearchTemplate extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 6156666017840435627L;

    @Schema(description = "名称")
    @Column(name = "name" ,length = 2000)
    @NotBlank(message = "{2000122}", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @Schema(description = "搜索内容-自由发挥")
    @Column(name = "content" ,length = 2000)
    private String content;

}
