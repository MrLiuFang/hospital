package com.lion.upms.dao.user;

import com.lion.core.LionPage;
import com.lion.upms.entity.user.User;
import org.springframework.data.domain.Page;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24下午2:14
 */
public interface UserDaoEx {

    /**
     * 类表
     * @param keyword
     * @param lionPage
     * @return
     */
    public Page<User> list(String keyword, LionPage lionPage);
}
