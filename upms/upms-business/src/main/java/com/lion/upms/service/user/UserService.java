package com.lion.upms.service.user;

import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.persistence.Validator;
import com.lion.core.service.BaseService;
import com.lion.upms.entity.role.vo.DetailsRoleUserVo;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.dto.AddUserDto;
import com.lion.upms.entity.user.vo.DetailsUserVo;
import com.lion.upms.entity.user.vo.ListUserVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    /**
     * 新增用户
     * @param addUserDto
     */
    public void add(AddUserDto addUserDto);

    /**
     * 列表
     * @param keyword
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListUserVo>> list(String keyword, LionPage lionPage);

    /**
     * 用户详情
     * @param id
     * @return
     */
    public DetailsUserVo details(Long id);
}
