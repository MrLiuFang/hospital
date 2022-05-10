package com.lion.manage.entity.license;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lion.core.persistence.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_license" )
@DynamicInsert
@Data
@Schema(description = "license")
public class License  extends BaseEntity implements Serializable {

    private LocalDate startDate;

    private LocalDate endDate;

    private Long userNum;

    @Column(length = 2000)
    private String menuList;

    @Column(length = 2000)
    private String workstationOrderList;

}
