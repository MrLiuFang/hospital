package com.lion.upms.expose.user;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.upms.entity.enums.State;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.user.User;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @description: 用户远程RPC暴露接口
 * @author: Mr.Liu
 * @create: 2020-01-19 10:50
 */
public interface UserExposeService extends BaseService<User> {

    /**
     * 创建user
     * @param user
     * @return
     */
    public User createUser(User user);

    /**
     * 根据用户名（登陆账号）查找用户
     * @param username
     * @return
     */
    public User find(String username);

    public int count(List<Long> ids, State deviceState);

    /**
     * 统计用户类型
     * @param userTypes
     * @return
     */
    public int countInUserType(Collection<UserType> userTypes);

    /**
     * 根据员工编号查询
     * @param number
     * @return
     */
    public User find(Integer number);

    /**
     * 根据姓名模糊查询
     * @param name
     * @return
     */
    public List<User> findByName(String name);

    /**
     *
     * @param ids
     * @return
     */
    public List<User> findInIds(List<Long> ids);

    /**
     *
     * @param name
     * @param ids
     * @return
     */
    public List<User> findByNameAndInIds(String name, List<Long> ids);

    /**
     *
     * @param departmentId
     * @param name
     * @param userType
     * @param ontIn
     * @param page
     * @param size
     * @return
     */
    public Map<String,Object> find(Long departmentId, String name, UserType userType, List<Long> ontIn, int page, int size);

    /**
     * 修改状态
     * @param id
     * @param state
     */
    public void updateState(Long id,Integer state);

    /**
     * 获取所有id
     * @return
     */
    public List<Long> allId();

    /**
     * 更新设备数据上传时间
     * @param id
     * @param dateTime
     */
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime);

}
