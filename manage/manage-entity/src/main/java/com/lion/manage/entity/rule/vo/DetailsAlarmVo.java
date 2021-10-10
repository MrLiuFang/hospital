package com.lion.manage.entity.rule.vo;

import com.lion.manage.entity.enums.AlarmWay;
import com.lion.manage.entity.rule.Alarm;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import javax.persistence.Column;
import java.awt.geom.PathIterator;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/13下午1:41
 */
@Data
@Schema
public class DetailsAlarmVo extends Alarm {

    @Schema(description = "管理员")
    private List<ManagerVo> managerVos;

    @Schema(description = "警报方式")
    private List<AlarmWay> ways;

    @Data
    @Schema
    public static class ManagerVo {

        @Schema(description = "姓名")
        private String name;

        @Schema(description = "id")
        private Long id;

        @Schema(description = "头像")
        private String headPortraitUrl;
    }
}
