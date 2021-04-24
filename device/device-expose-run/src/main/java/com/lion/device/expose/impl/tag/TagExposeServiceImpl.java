package com.lion.device.expose.impl.tag;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.tag.TagExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午9:05
 */
@DubboService(interfaceClass = TagExposeService.class)
public class TagExposeServiceImpl extends BaseServiceImpl<Tag> implements TagExposeService {

    @Autowired
    private TagDao tagDao;

    @Override
    public Tag find(Long assetsId) {
        return tagDao.findByAssetsId(assetsId);
    }

    @Override
    public Tag find(String tagCode) {
        return tagDao.findFirstByTagCode(tagCode);
    }
}
