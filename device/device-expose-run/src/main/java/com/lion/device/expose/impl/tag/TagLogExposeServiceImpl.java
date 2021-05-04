package com.lion.device.expose.impl.tag;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.enums.TagLogContent;
import com.lion.device.entity.tag.TagLog;
import com.lion.device.expose.tag.TagLogExposeService;
import com.lion.utils.CurrentUserUtil;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 下午5:13
 **/
@DubboService(interfaceClass = TagLogExposeService.class)
public class TagLogExposeServiceImpl extends BaseServiceImpl<TagLog> implements TagLogExposeService {

    @Override
    public void add(TagLogContent content, Long tagId) {
        TagLog tagLog = new TagLog();
        tagLog.setContent(content);
        tagLog.setTagId(tagId);
        tagLog.setUserId(CurrentUserUtil.getCurrentUserId());
        save(tagLog);
    }
}
