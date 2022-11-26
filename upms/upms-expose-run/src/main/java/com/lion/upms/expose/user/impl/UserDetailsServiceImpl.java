package com.lion.upms.expose.user.impl;

import com.lion.constant.DubboConstant;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.license.License;
import com.lion.manage.expose.license.LicenseExposeService;
import com.lion.security.LionSimpleGrantedAuthority;
import com.lion.security.LionUserDetails;
import com.lion.upms.entity.user.User;
import com.lion.upms.service.user.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: 用户接口暴露实现
 * @author: Mr.Liu
 * @create: 2020-01-17 10:29
 */
@DubboService(interfaceClass = UserDetailsService.class)
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @DubboReference
    private LicenseExposeService licenseExposeService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String ip = RpcContext.getServiceContext().getAttachment(DubboConstant.CLIENT_REMOTE_ADDRESS);
        if (StringUtils.hasText(ip)) {
            List<License> list = licenseExposeService.findAll();
            if (list.size()>0){
                License license = list.get(0);
                String workstationOrderList = license.getWorkstationOrderList();
                if (workstationOrderList.indexOf("mac") > -1 && workstationOrderList.indexOf(ip)<0) {
                    BusinessException.throwException("该ip禁止登陆");
                }
            }
        }
        User user =  userService.findUser(username);
        if (Objects.isNull(user)){
            return null;
        }
        LionUserDetails userDetails = new LionUserDetails(user.getUsername(),user.getPassword(),getUserGrantedAuthority(user.getId()));
        return userDetails;
    }

    /**
     * 获取用户权限
     * @param userId
     * @return
     */
    private List<GrantedAuthority> getUserGrantedAuthority(Long userId){
        List<GrantedAuthority> listGrantedAuthority = new ArrayList<GrantedAuthority>();
        return listGrantedAuthority;
    }
}
