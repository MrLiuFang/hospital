package com.lion.manage.entity.rule;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.lion.core.persistence.Validator;
//import com.lion.core.persistence.entity.BaseEntity;
//import com.lion.manage.entity.enums.WashRuleType;
//import io.swagger.v3.oas.annotations.media.Schema;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import org.hibernate.annotations.DynamicInsert;
//import org.hibernate.annotations.DynamicUpdate;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
//import java.io.Serializable;
//
///**
// * @author Mr.Liu
// * @Description:
// * @date 2021/4/9下午4:22
// */
//@EqualsAndHashCode(callSuper = true)
//@Entity
//@Table(name = "t_wash",indexes = {@Index(columnList = "name")})
//
//@DynamicInsert
//@Data
//@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
//@Schema(description = "洗手规则")
//public class Wash extends BaseEntity implements Serializable {
//
//    private static final long serialVersionUID = 6103915473684030413L;
//    @Schema(description = "规则名称")
//    @Column(name = "name")
//    @NotBlank(message = "{2000046}", groups = {Validator.Insert.class, Validator.Update.class})
//    private String name;
//
//    @Schema(description = "洗手规则类型")
//    @Column(name = "type")
//    @Convert(converter = WashRuleType.WashRuleTypeConverter.class)
//    @NotNull(message = "{2000047}", groups = {Validator.Insert.class, Validator.Update.class})
//    private WashRuleType type;
//
//    @Schema(description = "洗手间隔")
//    @Column(name = "interval")
//    private Integer interval;
//
//    @Schema(description = "洗手时长")
//    @Column(name = "duration")
//    private Integer duration;
//
//    @Schema(description = "是否提醒")
//    @Column(name = "remind")
//    @NotNull(message = "{2000048}", groups = {Validator.Insert.class, Validator.Update.class})
//    private Boolean remind;
//
//    @Schema(description = "超时提醒")
//    @Column(name = "overtime_remind")
//    private Integer overtimeRemind;
//
//    @Schema(description = "发起告警")
//    private Boolean isAlarm;
//
//    @Schema(description = "所有科室")
//    private Boolean isAllDepartment;
//
//    @Schema(description = "指定科室id,逗号隔开")
//    @Column(name = "department_id",length = 2000)
//    private String departmentIds;
//
//
////    @Schema(description = "进入之后X分钟需要洗手")
////    @Column(name = "after_entering_time")
////    private Integer afterEnteringTime;
////
////    @Schema(description = "进入之前X分钟需要洗手")
////    @Column(name = "before_entering_time")
////    private Integer beforeEnteringTime;
//
//    @Schema(description = "所有员工")
//    @Column(name = "is_all_user")
//    private Boolean isAllUser;
//
//    @Schema(description = "指定员工id,逗号隔开")
//    @Column(name = "user_id",length = 2000)
//    private String userIds;
//
//    @Schema(description = "备注")
//    @Column(name = "remarks")
//    private String remarks;
//}
