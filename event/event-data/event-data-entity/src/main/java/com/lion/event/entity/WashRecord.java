package com.lion.event.entity;

import com.lion.common.dto.WashRecordDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/5 上午8:42
 **/
@Data
@Document(value = "wash_record")
@ApiModel(value = "洗手记录(为减少mongo存储空间字段采用缩写方式)")
public class WashRecord extends WashRecordDto implements Serializable {

    private static final long serialVersionUID = -172159515038534622L;
}
