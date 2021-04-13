package com.lion.manage.entity.rule.vo;

import com.lion.manage.entity.rule.Alarm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel
public class DetailsAlarmVo extends Alarm {

    @ApiModelProperty(value = "管理员")
    private List<ManagerVo> managerVos;

    @Data
    @ApiModel
    public static class ManagerVo {

        @ApiModelProperty(value = "姓名")
        private String name;

        @ApiModelProperty(value = "id")
        private Long id;

        @ApiModelProperty(value = "头像")
        private String headPortraitUrl;
    }
}
