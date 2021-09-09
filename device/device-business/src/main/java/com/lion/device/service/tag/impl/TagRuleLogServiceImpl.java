package com.lion.device.service.tag.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.enums.TagRuleLogType;
import com.lion.device.entity.tag.TagRuleLog;
import com.lion.device.entity.tag.vo.ListTagRuleLogVo;
import com.lion.device.service.tag.TagRuleLogService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午11:06
 **/
@Service
public class TagRuleLogServiceImpl extends BaseServiceImpl<TagRuleLog> implements TagRuleLogService {

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Override
    public void add(Long tagRuleId, String content, TagRuleLogType tagRuleLogType) {
        TagRuleLog tagRuleLog  = new TagRuleLog();
        tagRuleLog.setContent(content);
        tagRuleLog.setTagRuleId(tagRuleId);
        tagRuleLog.setUserId(CurrentUserUtil.getCurrentUserId());
        tagRuleLog.setActionType(tagRuleLogType);
        save(tagRuleLog);
    }

    @Override
    public IPageResultData<List<ListTagRuleLogVo>> list(Long tagRuleId, LocalDateTime startDateTime, LocalDateTime endDateTime, TagRuleLogType tagRuleLogType, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(tagRuleId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_tagRuleId",tagRuleId);
        }
        if (Objects.nonNull(startDateTime)){
            jpqlParameter.setSearchParameter(SearchConstant.GREATER_THAN_OR_EQUAL_TO+"_createDateTime",startDateTime);
        }
        if (Objects.nonNull(endDateTime)){
            jpqlParameter.setSearchParameter(SearchConstant.LESS_THAN_OR_EQUAL_TO+"_createDateTime",endDateTime);
        }
        if (Objects.nonNull(tagRuleLogType)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_actionType",tagRuleLogType);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<TagRuleLog> page = findNavigator(lionPage);
        List<TagRuleLog> list = page.getContent();
        List<ListTagRuleLogVo> returnList = new ArrayList<ListTagRuleLogVo>();
        list.forEach(tagRuleLog -> {
            ListTagRuleLogVo vo = new ListTagRuleLogVo();
            vo.setContent(tagRuleLog.getContent());
            vo.setDateTime(tagRuleLog.getCreateDateTime());
            User user = userExposeService.findById(tagRuleLog.getUserId());
            if (Objects.nonNull(user)) {
                vo.setName(user.getName());
                vo.setHeadPortrait(user.getHeadPortrait());
                vo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                vo.setActionType(tagRuleLog.getActionType());
                returnList.add(vo);
            }

        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }
}
