package com.lion.upms.service.user;

import com.lion.core.service.BaseService;
import com.lion.upms.entity.role.vo.DetailsRoleUserVo;
import com.lion.upms.entity.user.User;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/22下午9:01
 */
public interface UserService extends BaseService<User> {
    /**
     * 根据登陆用户名获取用户
     * @param username 登陆用户名
     * @return
     */
    User findUser(String username);

    /**
     * 获取角色关联的用户
     * @param roleId
     * @return
     */
    public List<DetailsRoleUserVo> detailsRoleUser(Long roleId);
}
