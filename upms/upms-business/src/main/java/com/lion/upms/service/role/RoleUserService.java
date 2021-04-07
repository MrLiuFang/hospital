package com.lion.upms.service.role;

import com.lion.core.service.BaseService;
import com.lion.upms.entity.role.RoleUser;
import com.lion.upms.entity.role.vo.DetailsRoleUserVo;
import com.lion.upms.entity.role.vo.DetailsRoleVo;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/22下午9:14
 */
public interface RoleUserService extends BaseService<RoleUser> {

    /**
     * 根据角色id删除角色与用户的关联
     * @param roleId
     */
    public void deleteByRoleId(Long roleId);

    /**
     * 根据用户id删除角色与用户的关联
     * @param userId
     */
    public void deleteByUserId(Long userId);

    /**
     * 用户关联角色
     * @param userId
     * @param roleId
     */
    void relationRole(Long userId,Long roleId);
}
