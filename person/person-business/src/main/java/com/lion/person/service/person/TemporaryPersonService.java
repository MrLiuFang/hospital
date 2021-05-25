package com.lion.person.service.person;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.entity.person.dto.AddPatientDto;
import com.lion.person.entity.person.dto.AddTemporaryPersonDto;
import com.lion.person.entity.person.dto.UpdatePatientDto;
import com.lion.person.entity.person.dto.UpdateTemporaryPersonDto;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:09
 */
public interface TemporaryPersonService extends BaseService<TemporaryPerson> {

    /**
     * 新增流动人员
     * @param addTemporaryPersonDto
     */
    public void add(AddTemporaryPersonDto addTemporaryPersonDto);

    /**
     * 修改流动人员
     * @param updateTemporaryPersonDto
     */
    public void update(UpdateTemporaryPersonDto updateTemporaryPersonDto);

    /**
     * 删除
     * @param deleteDtos
     * @return
     */
    public void delete(List<DeleteDto> deleteDtos);
}
