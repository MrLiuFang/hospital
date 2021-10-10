package com.lion.manage.entity.rule.dto;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.lion.core.persistence.Validator;
//import com.lion.manage.entity.enums.WashDeviceType;
//import com.lion.manage.entity.rule.Wash;
//import io.swagger.v3.oas.annotations.media.Schema;
//
//import lombok.Data;
//
//import javax.validation.constraints.NotEmpty;
//import javax.validation.constraints.Size;
//import java.util.List;
//
///**
// * @author Mr.Liu
// * @Description:
// * @date 2021/4/9下午5:07
// */
//@Data
//@Schema
//@JsonIgnoreProperties(ignoreUnknown = true,value = {"createDateTime","updateDateTime","createUserId","updateUserId"})
//public class UpdateWashDto extends Wash {
//
//    @Schema(description = "区域id（全量，先删后增）")
////    @NotEmpty(message = "请选择区域",groups = {Validator.Update.class})
//    private List<Long> regionId;
//
//    @Schema(description = "用户id（全量，先删后增）")
//    private List<Long> userId;
//
//    @Schema(description = "洗手设备id（全量，先删后增）")
////    @NotEmpty( message = "请选择洗手设备",groups = {Validator.Update.class})
//    private List<Long> deviceId;
//
//    @Schema(description = "洗手设备类型(ISINFECTION_GEL(0, \"免洗消毒凝胶\"),LIQUID_SOAP(1, \"洗手液\"),ALCOHOL(2, \"酒精\"),WASHING_FOAM(3, \"洗手泡沫\"), WATER(4, \"清水\")) （全量，先删后增）")
//    private List<WashDeviceType> deviceTypes;
//
//}
