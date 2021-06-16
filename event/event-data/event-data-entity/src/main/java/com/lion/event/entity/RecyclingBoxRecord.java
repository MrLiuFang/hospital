package com.lion.event.entity;

import com.lion.common.dto.RecyclingBoxRecordDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午10:45
 */
@Data
@Document(value = "recycling_box_record")
public class RecyclingBoxRecord extends RecyclingBoxRecordDto {

    @Id
    private String _id;
}
