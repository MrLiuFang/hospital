package com.lion.upms.service.role;

import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.upms.entity.role.Role;
import com.lion.upms.entity.role.vo.PageRoleVo;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

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
    Page<PageRoleVo> list(String name, LionPage lionPage);

    /**
     * 判断角色名称是否存在
     * @param name
     * @param id
     */
    void assertNameExist(String name,Long id);

    /**
     * 根据用户获取角色
     * @param userId
     * @return
     */
    public Role findByUserId(Long userId);

    /**
     * 删除角色
     * @param deleteDtoList
     */
    public void delete( List<DeleteDto> deleteDtoList);


}
