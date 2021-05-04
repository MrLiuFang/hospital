package com.lion.device.expose.tag;

import com.lion.core.service.BaseService;
import com.lion.device.entity.enums.TagLogContent;
import com.lion.device.entity.tag.TagLog;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 下午5:11
 **/
public interface TagLogExposeService extends BaseService<TagLog> {

    /**
     * 添加日志
     * @param content
     * @param tagId
     */
    public void add(TagLogContent content, Long tagId );
}
