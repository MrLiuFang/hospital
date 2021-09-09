package com.lion.device.service.tag;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.device.entity.enums.TagLogContent;
import com.lion.device.entity.tag.TagLog;
import com.lion.device.entity.tag.vo.ListTagLogVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 下午5:01
 **/
public interface TagLogService extends BaseService<TagLog> {

    /**
     * 添加日志
     * @param content
     * @param tagId
     */
    public void add(TagLogContent content, Long tagId );

    /**
     * 列表
     * @param tagId
     * @param startDateTime
     * @param endDateTime
     * @param content
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListTagLogVo>> list(Long tagId, LocalDateTime startDateTime,LocalDateTime endDateTime,TagLogContent content, LionPage lionPage);

}
