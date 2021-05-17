package com.lion.device.service.tag.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.enums.TagLogContent;
import com.lion.device.entity.tag.TagLog;
import com.lion.device.entity.tag.vo.ListTagLogVo;
import com.lion.device.service.tag.TagLogService;
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
 * @Description //TODO
 * @Date 2021/5/4 下午5:02
 **/
@Service
public class TagLogServiceImpl extends BaseServiceImpl<TagLog> implements TagLogService {

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Override
    public void add(TagLogContent content, Long tagId) {
        TagLog tagLog = new TagLog();
        tagLog.setContent(content);
        tagLog.setTagId(tagId);
        tagLog.setUserId(CurrentUserUtil.getCurrentUserId());
        save(tagLog);
    }

    @Override
    public IPageResultData<List<ListTagLogVo>> list(Long tagId, LocalDateTime startDateTime, LocalDateTime endDateTime, TagLogContent content, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(tagId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_tagId",tagId);
        }
        if (Objects.nonNull(startDateTime)){
            jpqlParameter.setSearchParameter(SearchConstant.GREATER_THAN_OR_EQUAL_TO+"_createDateTime",startDateTime);
        }
        if (Objects.nonNull(endDateTime)){
            jpqlParameter.setSearchParameter(SearchConstant.LESS_THAN_OR_EQUAL_TO+"_createDateTime",endDateTime);
        }
        if (Objects.nonNull(content)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_content",content);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<TagLog> page = findNavigator(lionPage);
        List<TagLog> list = page.getContent();
        List<ListTagLogVo> returnList = new ArrayList<ListTagLogVo>();
        list.forEach(tagLog -> {
            ListTagLogVo vo = new ListTagLogVo();
            vo.setContent(tagLog.getContent());
            vo.setDateTime(tagLog.getCreateDateTime());
            User user = userExposeService.findById(tagLog.getUserId());
            if (Objects.nonNull(user)) {
                vo.setName(user.getName());
                vo.setNumber(user.getNumber());
                vo.setHeadPortrait(user.getHeadPortrait());
                vo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                returnList.add(vo);
            }

        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }
}
