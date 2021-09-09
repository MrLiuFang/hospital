package com.lion.device.service.tag;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.tag.TagRule;
import com.lion.device.entity.tag.dto.AddTagDto;
import com.lion.device.entity.tag.dto.AddTagRuleDto;
import com.lion.device.entity.tag.dto.UpdateTagDto;
import com.lion.device.entity.tag.dto.UpdateTagRuleDto;
import com.lion.device.entity.tag.vo.ListTagVo;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午11:04
 **/
public interface TagRuleService extends BaseService<TagRule> {

    /**
     * 新增标签规则
     * @param addTagRuleDto
     */
    public void add(AddTagRuleDto addTagRuleDto);

    /**
     * 修改标签规则
     * @param updateTagRuleDto
     */
    public void update(UpdateTagRuleDto updateTagRuleDto);

    /**
     * 删除标签规则
     * @param deleteDtoList
     */
    public void delete(List<DeleteDto> deleteDtoList);

    /**
     * 列表
     * @param battery
     * @param tagCode
     * @param type
     * @param purpose
     * @param lionPage
     * @return
     */
//    IPageResultData<List<ListTagVo>> list(Integer battery, String tagCode, TagType type, TagPurpose purpose, LionPage lionPage);
}
