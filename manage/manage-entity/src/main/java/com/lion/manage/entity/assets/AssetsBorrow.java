package com.lion.manage.entity.assets;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.Validator;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

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
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午2:34
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_assets_borrow",indexes = {@Index(columnList = "assets_id")} )

@DynamicInsert
@Data
@JsonIgnoreProperties(ignoreUnknown = true,value = {"updateDateTime","createUserId","updateUserId"})
@Schema(description = "资产借用")
public class AssetsBorrow extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7686080297795275063L;
    @Schema(description = "资产Id")
    @Column(name = "assets_id")
    @NotNull(message = "{2000005}", groups = {Validator.Insert.class})
    private Long assetsId;

    @Schema(description = "借用科室Id")
    @Column(name = "borrow_department_id")
    @NotNull(message = "{2000006}", groups = {Validator.Insert.class})
    private Long borrowDepartmentId;

    @Schema(description = "借用床位Id")
    @Column(name = "borrow_ward_room_sickbed_id")
    @NotNull(message = "{2000007}", groups = {Validator.Insert.class})
    private Long borrowWardRoomSickbedId;

    @Schema(description = "借用开始时间(yyyy-MM-dd HH:mm:ss)")
    @Column(name = "start_date_time")
    @NotNull(message = "{2000008}", groups = {Validator.Insert.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;

    @Schema(description = "借用结束时间(yyyy-MM-dd HH:mm:ss)")
    @Column(name = "end_date_time")
    @NotNull(message = "{2000009}", groups = {Validator.Insert.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    @Schema(description = "借用人")
    @Column(name = "borrow_user_id")
    @NotNull(message = "{2000010}", groups = {Validator.Insert.class})
    private Long borrowUserId;

    @Schema(description = "归还时间")
    @Column(name = "return_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime returnTime;

    @Schema(description = "归还人")
    @Column(name = "return_user_id")
    @NotNull(message = "归还时间不能为空", groups = {Validator.Update.class})
    private Long returnUserId;
}
