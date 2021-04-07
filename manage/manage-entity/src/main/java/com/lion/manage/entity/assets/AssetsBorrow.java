package com.lion.manage.entity.assets;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午2:34
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_assets_borrow",indexes = {@Index(columnList = "assets_id")} )
@DynamicUpdate
@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@ApiModel(description = "资产借用")
public class AssetsBorrow extends BaseEntity {

    @ApiModelProperty(value = "资产Id")
    @Column(name = "assets_id")
    @NotNull(message = "资产id不能为空", groups = {Validator.Insert.class, Validator.Update.class})
    private Long assetsId;

    @ApiModelProperty(value = "借用时间(yyyy-MM-dd HH:mm:ss)")
    @Column(name = "borrow_time",updatable = false)
    @NotNull(message = "借用时间不能为空", groups = {Validator.Insert.class})
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Past(message = "借用时间不能大于/等于当前时间", groups = {Validator.Insert.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime borrowTime;

    @ApiModelProperty(value = "借用人")
    @Column(name = "borrow_user_id",updatable = false)
    @NotNull(message = "借用人不能为空", groups = {Validator.Insert.class})
    private Long borrowUserId;

    @ApiModelProperty(value = "归还时间")
    @Column(name = "return_time",insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime returnTime;

    @ApiModelProperty(value = "归还人")
    @Column(name = "return_user_id",insertable = false)
    @NotNull(message = "归还时间不能为空", groups = {Validator.Update.class})
    private Long returnUserId;
}
