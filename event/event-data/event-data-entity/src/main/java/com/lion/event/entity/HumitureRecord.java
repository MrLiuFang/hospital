package com.lion.event.entity;

import com.lion.common.dto.HumitureRecordDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/16 下午5:36
 **/
@Data
@Document(value = "HUMITURE_RECORD")
public class HumitureRecord extends HumitureRecordDto implements Serializable {

    @Id
    private String _id;

}
