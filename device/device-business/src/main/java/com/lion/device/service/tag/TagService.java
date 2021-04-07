package com.lion.device.service.tag;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.dto.AddTagDto;
import com.lion.device.entity.tag.dto.UpdateTagDto;

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
}
