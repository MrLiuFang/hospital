package com.lion.device.expose.impl.tag;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagAssetsDao;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.entity.enums.TagLogContent;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagUseState;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagAssets;
import com.lion.device.expose.tag.TagAssetsExposeService;
import com.lion.device.service.tag.TagAssetsService;
import com.lion.device.service.tag.TagLogService;
import com.lion.device.service.tag.TagService;
import com.lion.exception.BusinessException;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午9:25
 */
@DubboService(interfaceClass = TagAssetsExposeService.class)
public class TagAssetsExposeServiceImpl extends BaseServiceImpl<TagAssets> implements TagAssetsExposeService {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private TagAssetsService tagAssetsService;

    @Autowired
    private TagAssetsDao tagAssetsDao;

    @Autowired
    private TagLogService tagLogService;

    @Override
    public Boolean relation(Long assetsId, String tagCode, Long departmentId) {
        Tag tag = tagDao.findFirstByTagCode(tagCode);
        if (Objects.isNull(tag)){
            BusinessException.throwException("该标签不存在");
        }
        if (!Objects.equals(tag.getPurpose(), TagPurpose.ASSETS)){
            BusinessException.throwException("该标签不能与资产关联");
        }
        if (!Objects.equals(tag.getDepartmentId(), departmentId)){
            BusinessException.throwException("该资产与标签不在同一科室,不能进行绑定");
        }
        TagAssets tagAssets = tagAssetsDao.findFirstByAssetsIdAndUnbindingTimeIsNull(assetsId);
        if (Objects.nonNull(tagAssets)){
            if (!Objects.equals( tagAssets.getAssetsId(), assetsId)){
                if (Objects.equals(tag.getUseState(), TagUseState.USEING)){
                    BusinessException.throwException("该标签正在使用中");
                }
            }else {
                return true;
            }
        }else {
            if (Objects.equals(tag.getUseState(), TagUseState.USEING)){
                BusinessException.throwException("该标签正在使用中");
            }
        }
        TagAssets newTagAssets = new TagAssets();
        newTagAssets.setAssetsId(assetsId);
        newTagAssets.setTagId(tag.getId());
        newTagAssets.setBindingTime(LocalDateTime.now());
        tagAssetsService.save(newTagAssets);
        tagLogService.add( TagLogContent.binding,tag.getId());
        tag.setUseState(TagUseState.USEING);
        tagService.update(tag);
        return true;
    }

    @Override
    public Boolean unrelation(Long assetsId) {
        TagAssets tagAssets = tagAssetsDao.findFirstByAssetsIdAndUnbindingTimeIsNull(assetsId);
        if (Objects.nonNull(tagAssets)){
            tagAssets.setUnbindingTime(LocalDateTime.now());
            tagAssetsService.update(tagAssets);
            Tag tag = tagService.findById(tagAssets.getTagId());
            if (Objects.nonNull(tag)){
                tag.setUseState(TagUseState.NOT_USED);
                tagService.update(tag);
                tagLogService.add( TagLogContent.unbinding,tagAssets.getTagId());
            }
        }
        return true;
    }

    @Override
    public Boolean deleteByAssetsId(Long assetsId) {
        unrelation(assetsId);
        tagAssetsDao.deleteByAssetsId(assetsId);
        return true;
    }

    @Override
    public TagAssets find(Long assetsId) {
        TagAssets tagAssets = tagAssetsDao.findFirstByAssetsIdAndUnbindingTimeIsNull(assetsId);
        return tagAssets;
    }

    @Override
    public TagAssets findByTagId(Long tagId) {
        return tagAssetsDao.findFirstByTagIdAndUnbindingTimeIsNull(tagId);
    }
}
