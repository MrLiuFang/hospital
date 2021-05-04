package com.lion.device.service.tag;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.device.entity.tag.TagRuleUser;
import com.lion.device.entity.tag.vo.ListTagRuleUserVo;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.user.User;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 上午11:05
 **/
public interface TagRuleUserService extends BaseService<TagRuleUser> {

    /**
     * 关联关联用户
     * @param newUser
     * @param deleteUser
     * @param tagRuleId
     */
    public void relationUser(List<Long> newUser,List<Long> deleteUser,Long tagRuleId);

    /**
     * 关联的用户列表
     * @param tagRuleId
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListTagRuleUserVo>> list(Long tagRuleId, LionPage lionPage);

    /**
     * 查询可关联的用户
     * @param departmentId
     * @param name
     * @param userType
     * @param lionPage
     * @return
     */
    public IPageResultData<List<User>>  ruleUserSearch(Long departmentId, String name, UserType userType,LionPage lionPage);
}
