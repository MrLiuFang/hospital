package com.lion.manage.service.rule;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.rule.Wash;
import com.lion.manage.entity.rule.dto.AddWashDto;
import com.lion.manage.entity.rule.dto.UpdateWashDto;
import com.lion.manage.entity.rule.vo.DetailsWashVo;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午4:49
 */
public interface WashService extends BaseService<Wash> {

    /**
     * 新增洗手规则
     * @param addWashDto
     */
    public void add(AddWashDto addWashDto);

    /**
     * 更新
     * @param updateWashDto
     */
    public void update(UpdateWashDto updateWashDto);

    /**
     * 详情
     * @param id
     * @return
     */
    public DetailsWashVo details(Long id);

    /**
     * 删除
     * @param deleteDtos
     */
    public void delete(List<DeleteDto> deleteDtos);
}
