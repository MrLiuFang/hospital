package com.lion.device.service.tag;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.enums.TagUseState;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.dto.AddTagDto;
import com.lion.device.entity.tag.dto.UpdateTagDto;
import com.lion.device.entity.tag.vo.ListTagVo;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午8:15
 */
public interface TagService extends BaseService<Tag> {

    /**
     * 新增标签
     * @param addTagDto
     */
    public void add(AddTagDto addTagDto);

    /**
     * 修改标签
     * @param updateTagDto
     */
    public void update(UpdateTagDto updateTagDto);

    /**
     * 删除标签
     * @param deleteDtoList
     */
    public void delete(List<DeleteDto> deleteDtoList);

    /**
     * 列表
     *
     *
     * @param departmentId
     * @param useState
     * @param battery
     * @param tagCode
     * @param type
     * @param purpose
     * @param lionPage
     * @return
     */
    IPageResultData<List<ListTagVo>> list(Long departmentId,TagUseState useState,Integer battery, String tagCode, TagType type, TagPurpose purpose, LionPage lionPage);

    /**
     * 获取所有数据的id
     * @return
     */
    public List<Long> allId();
}
