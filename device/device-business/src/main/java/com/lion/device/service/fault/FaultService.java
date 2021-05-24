package com.lion.device.service.fault;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.device.entity.enums.FaultType;
import com.lion.device.entity.fault.Fault;
import com.lion.device.entity.fault.dto.AddFaultDto;
import com.lion.device.entity.fault.dto.UpdateFaultDto;
import com.lion.device.entity.fault.vo.FaultDetailsVo;
import com.lion.device.entity.fault.vo.ListFaultVo;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.PrimitiveIterator;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 上午8:46
 */
public interface FaultService extends BaseService<Fault> {

    /**
     * 新增
     * @param addFaultDto
     */
    public void save(AddFaultDto addFaultDto);

    /**
     * 修改
     * @param updateFaultDto
     */
    public void update(UpdateFaultDto updateFaultDto);

    /**
     * 详情
     * @param id
     * @return
     */
    public FaultDetailsVo details(Long id);

    /**
     * 设置vo信息
     * @param vo
     * @param code
     * @param lionPage
     * @return
     */
    public FaultDetailsVo setInfoVo(FaultDetailsVo vo);

    /**
     * 列表
     * @param type
     * @param code
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListFaultVo>> list(FaultType type, String code, LionPage lionPage);
}
