package com.lion.person.entity.person;
//
//import com.lion.core.persistence.Validator;
//import com.lion.core.persistence.entity.BaseEntity;
//import io.swagger.v3.oas.annotations.media.Schema;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import org.hibernate.annotations.DynamicInsert;
//import org.hibernate.annotations.DynamicUpdate;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Index;
//import javax.persistence.Table;
//import javax.validation.constraints.NotNull;
//import java.io.Serializable;
//
///**
// * @description:
// * @author: Mr.Liu
// * @time: 2021/6/14 下午8:35
// */
//@EqualsAndHashCode(callSuper = true)
//@Entity
//@Table(name = "t_patient_doctor",indexes = {@Index(columnList = "patient_id")})
//
//@DynamicInsert
//@Data
//@Schema(description = "患者负责的医生")
//public class PatientDoctor extends BaseEntity implements Serializable {
//
//    private static final long serialVersionUID = 786096266217805032L;
//    @Schema(description = "患者ID")
//    @Column(name = "patient_id")
//    @NotNull(message = "{患者不能为空}", groups = {Validator.Insert.class, Validator.Update.class})
//    private Long patientId;
//
//    @Schema(description = "负责医生")
//    @Column(name = "doctor_id")
//    @NotNull(message = "{1000006}", groups = {Validator.Insert.class, Validator.Update.class})
//    private Long doctorId;
//}
