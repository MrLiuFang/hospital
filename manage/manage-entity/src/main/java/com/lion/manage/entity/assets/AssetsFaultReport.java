package com.lion.manage.entity.assets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2021-08-09 10:13
 **/
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_assets_fault_report",indexes = {@Index(columnList = "assets_fault_id")}  )
@DynamicUpdate
@DynamicInsert
@Data
@ApiModel(description = "资产故障汇报")
public class AssetsFaultReport extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -311081976548403331L;

    @ApiModelProperty(value = "资产故障iId")
    @Column(name = "assets_fault_id")
    @NotNull(message = "{2000015}", groups = {Validator.Insert.class, Validator.Update.class})
    private Long assetsFaultId;

    @ApiModelProperty(value = "汇报")
    @Column(name = "report")
    @NotNull(message = "{2000016}", groups = {Validator.Insert.class, Validator.Update.class})
    @Length(max = 250,message ="{2000017}" , groups = {Validator.Insert.class, Validator.Update.class})
    private String report;
}
