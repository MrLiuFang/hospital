package com.lion.device.expose.tag;

import com.lion.core.service.BaseService;
import com.lion.device.entity.tag.TagPostdocs;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:37
 */
public interface TagPostdocsExposeService extends BaseService<TagPostdocs> {

    /** 患者与标签绑定
     * @param postdocsId
     * @param tagCode
     */
    public void binding(Long postdocsId, String tagCode);

    /**
     * 患者与标签解绑
     * @param postdocsId
     * @param isDelete
     */
    public void unbinding(Long postdocsId,Boolean isDelete);
}
