package com.lion.manage.service.rule;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.rule.Alarm;
import com.lion.manage.entity.rule.dto.AddAlarmDto;
import com.lion.manage.entity.rule.dto.AddWashDto;
import com.lion.manage.entity.rule.dto.UpdateAlarmDto;
import com.lion.manage.entity.rule.dto.UpdateWashDto;
import com.lion.manage.entity.rule.vo.DetailsAlarmVo;
import com.lion.manage.entity.rule.vo.DetailsWashVo;
import com.lion.manage.entity.rule.vo.ListAlarmVo;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/13下午1:36
 */
public interface AlarmService extends BaseService<Alarm> {

    /**
     * 新增警报规则
     * @param addAlarmDto
     */
    public void add(AddAlarmDto addAlarmDto);

    /**
     * 更新
     * @param updateAlarmDto
     */
    public void update(UpdateAlarmDto updateAlarmDto);

    /**
     * 详情
     * @param id
     * @return
     */
    public DetailsAlarmVo details(Long id);

    /**
     * 列表
     * @param content
     * @param classify
     * @param level
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListAlarmVo>> list( String content,AlarmClassify classify, Integer level, LionPage lionPage);

    /**
     * 删除
     * @param deleteDtos
     */
    public void delete(List<DeleteDto> deleteDtos);
}
