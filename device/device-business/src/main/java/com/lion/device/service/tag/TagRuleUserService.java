package com.lion.device.service.tag;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.device.entity.tag.TagRuleUser;
import com.lion.device.entity.tag.vo.ListTagRuleUserVo;
import com.lion.upms.entity.user.User;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午11:05
 **/
public interface TagRuleUserService extends BaseService<TagRuleUser> {

    /**
     * 关联关联用户
     * @param newUser
     * @param deleteUser
     * @param allUser
     * @param tagRuleId
     */
    public void relationUser(List<Long> newUser,List<Long> deleteUser,List<Long> allUser,Long tagRuleId);

    /**
     * 关联的用户列表
     * @param tagRuleId
     * @param lionPage
     * @return
     */
    public Page list(Long tagRuleId, LionPage lionPage);

    /**
     * 查询可关联的用户
     * @param departmentId
     * @param name
     * @param userTypeId
     * @param lionPage
     * @return
     */
    public IPageResultData<List<User>>  ruleUserSearch(Long departmentId, String name, Long userTypeId,LionPage lionPage);

    /**
     * 根据规则id查询关联的用户
     * @param tagRuleId
     * @return
     */
    public List<TagRuleUser> find(Long tagRuleId);
}
