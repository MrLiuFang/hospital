package com.lion.upms.expose.role;

import com.lion.core.service.BaseService;
import com.lion.upms.entity.role.Role;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/9 下午8:10
 */
public interface RoleExposeService extends BaseService<Role> {

    /**
     * 查询用户角色
     * @param userId
     * @return
     */
    public Role find(Long userId);
}
