package com.lion.device.service.tag.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagRuleUserDao;
import com.lion.device.entity.tag.TagRuleUser;
import com.lion.device.entity.tag.vo.ListTagRuleUserVo;
import com.lion.device.service.tag.TagRuleUserService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 上午11:07
 **/
@Service
public class TagRuleUserServiceImpl extends BaseServiceImpl<TagRuleUser> implements TagRuleUserService {

    @Autowired
    private TagRuleUserDao tagRuleUserDao;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @Override
    public void relationUser(List<Long> newUser, List<Long> deleteUser, Long tagRuleId) {
        if (Objects.nonNull(deleteUser)) {
            deleteUser.forEach(id->{
                tagRuleUserDao.deleteByUserIdAndAndTagRuleId(id,tagRuleId);
            });
        }

        if (Objects.nonNull(newUser)) {
            newUser.forEach(id->{
                TagRuleUser tagRuleUser = tagRuleUserDao.findFirstByUserIdAndTagRuleIdNot(id, tagRuleId);
                if (Objects.nonNull(tagRuleUser)) {
                    User user = userExposeService.findById(id);
                    if (Objects.nonNull(user)){
                        BusinessException.throwException(user.getName() + "已关联其它标签规则");
                    }
                }
                tagRuleUserDao.deleteByUserIdAndAndTagRuleId(id,tagRuleId);
                TagRuleUser newTagRuleUser = new TagRuleUser();
                newTagRuleUser.setUserId(id);
                newTagRuleUser.setTagRuleId(tagRuleId);
                save(newTagRuleUser);
            });
        }
    }

    @Override
    public IPageResultData<List<ListTagRuleUserVo>> list(Long tagRuleId, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(tagRuleId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_tagRuleId",tagRuleId);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        Page<TagRuleUser> page = findNavigator(lionPage);
        List<TagRuleUser> list = page.getContent();
        List<ListTagRuleUserVo> returnList = new ArrayList<>();
        list.forEach(tagRuleUser -> {
            ListTagRuleUserVo vo = new ListTagRuleUserVo();
            User user = userExposeService.findById(tagRuleUser.getUserId());
            if (Objects.nonNull(user)){
                returnList.add(vo);
                vo.setName(user.getName());
                vo.setId(user.getId());
                vo.setHeadPortrait(user.getHeadPortrait());
                vo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                vo.setNumber(user.getNumber());
                vo.setPosition(user.getUserType().getDesc());
                Department department = departmentUserExposeService.findDepartment(user.getId());
                if (Objects.nonNull(department)) {
                    vo.setDepartmentName(department.getName());
                }
            }

        });
        return new PageResultData<List<ListTagRuleUserVo>>(returnList,page.getPageable(),page.getTotalElements());
    }

    @Override
    public PageResultData<List<User>> ruleUserSearch(Long departmentId, String name, UserType userType, LionPage lionPage) {
        List<Long> userList = new ArrayList<Long>();
        List<TagRuleUser> list = findAll();
        list.forEach(tagRuleUser -> {
            userList.add(tagRuleUser.getUserId());
        });
        Map<String,Object> map = userExposeService.find(departmentId,name,userType,userList,lionPage.getPageNumber(),lionPage.getPageSize());
        return new PageResultData<List<User>>((List) map.get("list"),lionPage,(Long) map.get("totalElements"));
    }
}
