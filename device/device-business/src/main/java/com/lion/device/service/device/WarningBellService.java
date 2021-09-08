package com.lion.device.service.device;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.device.entity.device.WarningBell;
import com.lion.device.entity.device.dto.AddWarningBellDto;
import com.lion.device.entity.device.dto.UpdateWarningBellDto;
import com.lion.device.entity.device.vo.DetailsWarningBellVo;
import com.lion.device.entity.device.vo.ListWarningBellVo;
import com.lion.upms.entity.user.vo.ListUserTypeVo;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 下午1:57
 */
public interface WarningBellService extends BaseService<WarningBell> {

    /**
     * 新增
     * @param addWarningBellDto
     */
    public void add(AddWarningBellDto addWarningBellDto);

    /**
     * 修改
     * @param updateWarningBellDto
     */
    public void update(UpdateWarningBellDto updateWarningBellDto);

    /**
     * 删除
     * @param deleteDto
     */
    public void delete(List<DeleteDto> deleteDto);

    /**
     * 列表
     * @param name
     * @param warningBellId
     * @param departmentId
     * @param LionPage
     * @return
     */
    public IPageResultData<List<ListWarningBellVo>> list(String name, String code, String warningBellId, Long departmentId, LionPage LionPage);

    /**
     * 详情
     * @param id
     * @return
     */
    public DetailsWarningBellVo details(Long id);
}
