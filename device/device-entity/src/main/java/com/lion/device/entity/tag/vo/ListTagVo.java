package com.lion.device.entity.tag.vo;

import com.lion.device.entity.tag.Tag;
import lombok.Data;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 上午9:18
 **/
@Data
public class ListTagVo extends Tag {

    /**
     * 绑定对象
     */
    private String bindingName = "默认值";

    /**
     * 科室
     */
    private String departmentName;
}
