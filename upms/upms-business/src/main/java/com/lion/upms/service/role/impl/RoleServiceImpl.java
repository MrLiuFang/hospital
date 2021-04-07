package com.lion.upms.service.role.impl;

import com.lion.constant.SearchConstant;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.upms.dao.role.RoleDao;
import com.lion.upms.dao.role.RoleUserDao;
import com.lion.upms.entity.role.Role;
import com.lion.upms.entity.role.vo.PageRoleVo;
import com.lion.upms.service.role.RoleService;
import com.lion.upms.service.role.RoleUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/22下午9:12
 */
@Service
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleUserDao roleUserDao;

    @Autowired
    private RoleUserService roleUserService;

    @Override
    public Page<PageRoleVo> list(String name, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<Role> page = findNavigator(lionPage);
        Page<PageRoleVo> returnPage = new PageImpl<PageRoleVo>(convertVo(page.getContent()),lionPage,page.getTotalElements());
        return returnPage;
    }

    @Override
    public void assertNameExist(String name, Long id) {
        Role role = roleDao.findFirstByName(name);
        if (Objects.isNull(id) && Objects.nonNull(role) ){
            BusinessException.throwException("该角色名称已存在");
        }
        if (Objects.nonNull(id) && Objects.nonNull(role) && !role.getId().equals(id)){
            BusinessException.throwException("该角色名称已存在");
        }
    }

    @Override
    public Role findByUserId(Long userId) {
        return roleDao.findByUserId(userId);
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(d->{
            Role role = this.findById(d.getId());
            if (Objects.nonNull(role) && !role.getIsDefault()) {
                deleteById(d.getId());
                roleUserService.deleteByRoleId(d.getId());
            }
        });
    }

    private List<PageRoleVo> convertVo(List<Role> list){
        List<PageRoleVo> returnList = new ArrayList<PageRoleVo>();
        list.forEach(role -> {
            PageRoleVo pageRoleVo = new PageRoleVo();
            BeanUtils.copyProperties(role,pageRoleVo);
            pageRoleVo.setUserCount(roleUserDao.countByRoleId(role.getId()));
            returnList.add(pageRoleVo);
        });
        return returnList;
    }
}
