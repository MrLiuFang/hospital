package com.lion.person.service.person;

import com.lion.core.service.BaseService;
import com.lion.person.entity.person.TempLeave;
import com.lion.person.entity.person.dto.AddTempLeaveDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午9:24
 */
public interface TempLeaveService extends BaseService<TempLeave> {

    /**
     * 新增临时离开
     * @param addTempLeaveDto
     */
    public void addTempLeave(@RequestBody @Validated AddTempLeaveDto addTempLeaveDto);
}
