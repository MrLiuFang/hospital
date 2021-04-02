package com.lion.manage.service.ward;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.ward.Ward;
import com.lion.manage.entity.ward.dto.AddWardDto;
import com.lion.manage.entity.ward.dto.UpdateWardDto;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:12
 */
public interface WardService extends BaseService<Ward> {
    /**
     * 根据科室删除
     * @param departmentId
     * @return
     */
    public int deleteByDepartmentId(Long departmentId);

    /**
     * 新增病房
     * @param addWardDto
     */
    public void add(AddWardDto addWardDto);

    /**
     * 修改病房
     * @param updateWardDto
     */
    public void update(UpdateWardDto updateWardDto);

    /**
     * 删除病房
     * @param deleteDtoList
     */
    public void delete(List<DeleteDto> deleteDtoList);

}
