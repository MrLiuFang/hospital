package com.lion.upms.service.user;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.upms.entity.role.vo.DetailsRoleUserVo;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.dto.AddUserDto;
import com.lion.upms.entity.user.dto.UpdateUserDto;
import com.lion.upms.entity.user.vo.DetailsUserVo;
import com.lion.upms.entity.user.vo.ListUserVo;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.IOException;
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
     *
     * @param departmentId
     * @param userTypeIds
     * @param number
     * @param name
     * @param roleId
     * @param isAdmin
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListUserVo>> list(Long departmentId, Long userTypeIds, Integer number, String name, Long roleId,Boolean isAdmin, LionPage lionPage);

    /**
     * 导出
     * @param departmentId
     * @param userTypeIds
     * @param number
     * @param name
     * @param roleId
     */
    public void export(Long departmentId, Long userTypeIds, Integer number, String name, Long roleId) throws IOException, IllegalAccessException;

    /**
     * 导入
     */
    public void importUser(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException;

    /**
     * 用户详情
     * @param id
     * @return
     */
    public DetailsUserVo details(Long id);

    /**
     * 修改用户
     * @param updateUserDto
     */
    public void update(UpdateUserDto updateUserDto);

    /**
     * 删除用户
     * @param deleteDtoList
     */
    public void delete(List<DeleteDto> deleteDtoList);
}
