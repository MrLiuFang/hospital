package com.lion.device.service.tag.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagRuleUserDao;
import com.lion.device.entity.enums.TagRuleLogType;
import com.lion.device.entity.tag.TagRule;
import com.lion.device.entity.tag.TagRuleUser;
import com.lion.device.entity.tag.vo.ListTagRuleUserVo;
import com.lion.device.service.tag.TagRuleLogService;
import com.lion.device.service.tag.TagRuleService;
import com.lion.device.service.tag.TagRuleUserService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.UserType;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.upms.expose.user.UserTypeExposeService;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description
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

    @Autowired
    private TagRuleLogService tagRuleLogService;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @DubboReference
    private UserTypeExposeService userTypeExposeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TagRuleService tagRuleService;

    @Override
    @Transactional
    public void relationUser(List<Long> newUser, List<Long> deleteUser,List<Long> allUser, Long tagRuleId) {
        if (Objects.nonNull(allUser)){
            allUser.forEach(id->{
                TagRuleUser tagRuleUser = tagRuleUserDao.findFirstByUserIdAndTagRuleIdNot(id, tagRuleId);
                if (Objects.nonNull(tagRuleUser)) {
                    com.lion.core.Optional<User> optional = userExposeService.findById(id);
                    if (optional.isPresent()){
                        BusinessException.throwException(optional.get().getName() + MessageI18nUtil.getMessage("4000043"));
                    }
                }
            });
            List<TagRuleUser> list = tagRuleUserDao.findByTagRuleId(tagRuleId);
            list.forEach(tagRuleUser -> {
                if (!allUser.contains(tagRuleUser.getUserId())){
                    com.lion.core.Optional<User> optionalUser = userExposeService.findById(tagRuleUser.getUserId());
                    com.lion.core.Optional<TagRule> optionalTagRule = tagRuleService.findById(tagRuleUser.getTagRuleId());
                    if (optionalUser.isPresent() && optionalTagRule.isPresent()) {
                        tagRuleLogService.add(tagRuleId, optionalUser.get().getName() + "从规则(" + optionalTagRule.get().getName() + ")中删除", TagRuleLogType.DELETE_USER);
                    }
                }
                redisTemplate.delete(RedisConstants.USER_TAG_RULE+tagRuleUser.getUserId());
            });
            tagRuleUserDao.deleteByTagRuleId(tagRuleId);
            save(allUser,tagRuleId,list );
            return;
        }

        if (Objects.nonNull(deleteUser)) {
            deleteUser.forEach(id->{
                redisTemplate.delete(RedisConstants.USER_TAG_RULE+id);
                tagRuleUserDao.deleteByUserIdAndAndTagRuleId(id,tagRuleId);
                com.lion.core.Optional<User> optional = userExposeService.findById(id);
                if (optional.isPresent()){
                    com.lion.core.Optional<TagRule> optionalTagRule = tagRuleService.findById(tagRuleId);
                    if (optionalTagRule.isPresent())
                    tagRuleLogService.add(tagRuleId,optional.get().getName()+"从规则("+optionalTagRule.get().getName()+")中删除", TagRuleLogType.DELETE_USER);
                }
            });
        }
        save(newUser,tagRuleId, null);

    }

    private void save(List<Long> newUser,Long tagRuleId,List<TagRuleUser> list){
        if (Objects.nonNull(newUser)) {
            newUser.forEach(id->{
                TagRuleUser tagRuleUser = tagRuleUserDao.findFirstByUserIdAndTagRuleIdNot(id, tagRuleId);
                if (Objects.nonNull(tagRuleUser)) {
                    com.lion.core.Optional<User> optional = userExposeService.findById(id);
                    if (optional.isPresent()){
                        BusinessException.throwException(optional.get().getName() + MessageI18nUtil.getMessage("4000043"));
                    }
                }
                tagRuleUserDao.deleteByUserIdAndAndTagRuleId(id,tagRuleId);
                TagRuleUser newTagRuleUser = new TagRuleUser();
                newTagRuleUser.setUserId(id);
                newTagRuleUser.setTagRuleId(tagRuleId);
                save(newTagRuleUser);
                com.lion.core.Optional<User> optionalUser = userExposeService.findById(id);
                com.lion.core.Optional<TagRule> optionalTagRule = tagRuleService.findById(tagRuleId);
                if (optionalUser.isPresent() && Objects.isNull(list) && optionalTagRule.isPresent()){
                    tagRuleLogService.add(tagRuleId,optionalUser.get().getName()+"添加到规则("+optionalTagRule.get().getName()+")中", TagRuleLogType.ADD_USER);
                }else if (Objects.nonNull(list) && !list.contains(id)) {
                    tagRuleLogService.add(tagRuleId,(optionalUser.isPresent()?optionalUser.get().getName():"")+"添加到规则("+(optionalTagRule.isPresent()?optionalTagRule.get().getName():"")+")中", TagRuleLogType.ADD_USER);
                }
                redisTemplate.opsForValue().set(RedisConstants.USER_TAG_RULE+id,tagRuleId,5, TimeUnit.MINUTES);
            });
        }
    }

    @Override
    public Page list(Long tagRuleId, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(tagRuleId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_tagRuleId",tagRuleId);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<TagRuleUser> page = findNavigator(lionPage);
        List<TagRuleUser> list = page.getContent();
        List<ListTagRuleUserVo> returnList = new ArrayList<>();
        list.forEach(tagRuleUser -> {
            ListTagRuleUserVo vo = new ListTagRuleUserVo();
            com.lion.core.Optional<User> optional = userExposeService.findById(tagRuleUser.getUserId());
            if (optional.isPresent()){
                User user = optional.get();
                vo.setName(user.getName());
                vo.setId(user.getId());
                vo.setHeadPortrait(user.getHeadPortrait());
                vo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                vo.setNumber(user.getNumber());
                com.lion.core.Optional<UserType> optionalUserType = userTypeExposeService.findById(user.getUserTypeId());
                if (optionalUserType.isPresent()) {
                    vo.setPosition(optionalUserType.get().getUserTypeName());
                }
                Department department = departmentUserExposeService.findDepartment(user.getId());
                if (Objects.nonNull(department)) {
                    vo.setDepartmentName(department.getName());
                }
                returnList.add(vo);
            }

        });
        PageResultData  pageResultData = new PageResultData(returnList,page.getPageable(),page.getTotalElements());
        return pageResultData;
    }

    @Override
    public PageResultData<List<User>> ruleUserSearch(Long departmentId, String name, Long userTypeId, LionPage lionPage) {
        List<Long> userList = new ArrayList<Long>();
        List<TagRuleUser> list = findAll();
        list.forEach(tagRuleUser -> {
            userList.add(tagRuleUser.getUserId());
        });
        Map<String,Object> map = userExposeService.find(departmentId,name, userTypeId,userList,lionPage.getPageNumber(),lionPage.getPageSize());
        return new PageResultData<List<User>>((List) map.get("list"),lionPage,(Long) map.get("totalElements"));
    }

    @Override
    public List<TagRuleUser> find(Long tagRuleId) {
        return tagRuleUserDao.findByTagRuleId(tagRuleId);
    }
}
