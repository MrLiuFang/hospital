package com.lion.manage.entity.build;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31上午10:02
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_build_floor" )

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
@Schema(description = "楼层")
public class BuildFloor extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 4430569449863892569L;
    @Schema(description = "建筑id")
    @NotNull(message = "{2000022}", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "build_id")
    private Long buildId;

    @Schema(description = "地图url")
    @NotBlank(message = "{2000023}", groups = {Validator.Insert.class, Validator.Update.class})
    @Column(name = "map_url")
    private String mapUrl;

    @Schema(description = "楼层名称")
    @Column(name = "name")
    @NotBlank(message = "{2000024}", groups = {Validator.Insert.class, Validator.Update.class})
    private String name;

    @Schema(description = "地图高")
    @Column(name = "map_height")
    private BigDecimal mapHeight;

    @Schema(description = "地图宽")
    @Column(name = "map_width")
    private BigDecimal mapWidth;

    @Schema(description = "放大级别")
    @Column(name = "level")
    private Integer level;

    @Schema(description = "备注")
    @Column(name = "remarks")
    private String remarks;
}
