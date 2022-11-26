package com.lion.device.service.tag;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.device.entity.enums.State;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.dto.AddTagDto;
import com.lion.device.entity.tag.dto.UpdateTagDto;
import com.lion.device.entity.tag.vo.DetailsTagVo;
import com.lion.device.entity.tag.vo.ListTagVo;
import com.lion.device.entity.tag.vo.PurposeStatisticsVo;
import com.lion.device.entity.tag.vo.TagStatisticsVo;

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
     *
     * @param deleteDtoList
     * @return
     */
    public List<Tag> delete(List<DeleteDto> deleteDtoList);

    /**
     * 列表
     *
     * @param isResponsibleDepartment
     * @param isAll
     * @param isTmp
     * @param departmentId
     * @param useState
     * @param state
     * @param battery
     * @param tagCode
     * @param type
     * @param purpose
     * @param lionPage
     * @return
     */
    IPageResultData<List<ListTagVo>> list(Boolean isResponsibleDepartment,Boolean isAll,String isTmp, Long departmentId,String useState, State state, Integer battery, String tagCode, TagType type, TagPurpose purpose, LionPage lionPage);

    /**
     * 获取所有数据的id
     * @return
     */
    public List<Long> allId();

    /**
     * 详情
     * @param id
     * @return
     */
    public DetailsTagVo details(Long id);

    /**
     * 标签统计
     * @return
     */
    public List<PurposeStatisticsVo> purposeStatistics();

    public TagStatisticsVo statistics();
}
