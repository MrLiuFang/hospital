package com.lion.upms.service.role;

import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.upms.entity.role.Role;
import com.lion.upms.entity.role.vo.PageRoleVo;
import org.springframework.data.domain.Page;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/22下午9:12
 */
public interface RoleService extends BaseService<Role> {

    /**
     * 角色列表
     * @param name
     * @param lionPage
     * @return
     */
    Page<PageRoleVo> page(String name, LionPage lionPage);

    /**
     * 判断角色名称是否存在
     * @param name
     * @param id
     */
    void assertNameExist(String name,Long id);
}
