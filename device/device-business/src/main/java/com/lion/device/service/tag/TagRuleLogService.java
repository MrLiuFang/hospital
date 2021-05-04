package com.lion.device.service.tag;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.device.entity.tag.TagRuleLog;
import com.lion.device.entity.tag.vo.ListTagRuleLogVo;
import org.springframework.context.annotation.Primary;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 上午11:04
 **/
public interface TagRuleLogService extends BaseService<TagRuleLog> {

    /**
     * 新增记录
     * @param tagRuleId
     * @param content
     */
    public void add(Long tagRuleId,String content);

    /**
     * 列表
     * @param tagRuleId
     * @param lionPage
     * @return
     */
    IPageResultData<List<ListTagRuleLogVo>> list(Long tagRuleId, LionPage lionPage);
}
