package com.lion.manage.service.department.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentResponsibleUserDao;
import com.lion.manage.entity.department.DepartmentResponsibleUser;
import com.lion.manage.entity.department.vo.ResponsibleUserVo;
import com.lion.manage.service.department.DepartmentResponsibleUserService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/23下午8:16
 */
@Service
public class DepartmentResponsibleUserServiceImpl extends BaseServiceImpl<DepartmentResponsibleUser> implements DepartmentResponsibleUserService {

    @Autowired
    private DepartmentResponsibleUserDao departmentResponsibleUserDao;


    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;


    @Override
    public void save(List<Long> responsible, Long departmentId) {
        responsible.forEach(id->{
            DepartmentResponsibleUser departmentResponsibleUser = new DepartmentResponsibleUser();
            departmentResponsibleUser.setDepartmentId(departmentId);
            departmentResponsibleUser.setUserId(id);
            departmentResponsibleUserDao.save(departmentResponsibleUser);
        });
    }

    @Override
    public int deleteByDepartmentId(Long departmentId) {
        return departmentResponsibleUserDao.deleteByDepartmentId(departmentId);
    }

    @Override
    public void relationDepartment(Long userId, List<Long> departmentIds) {
        departmentResponsibleUserDao.deleteByUserId(userId);
        if (Objects.nonNull(departmentIds) && departmentIds.size()>0) {
            departmentIds.forEach(id -> {
                DepartmentResponsibleUser departmentResponsibleUser = new DepartmentResponsibleUser();
                departmentResponsibleUser.setDepartmentId(id);
                departmentResponsibleUser.setUserId(userId);
                departmentResponsibleUserDao.save(departmentResponsibleUser);
            });
        }
    }

    @Override
    public List<ResponsibleUserVo> responsibleUser(Long departmentId){
        List<DepartmentResponsibleUser> list = departmentResponsibleUserDao.findByDepartmentId(departmentId);
        List<ResponsibleUserVo> returnList = new ArrayList<ResponsibleUserVo>();
        list.forEach(departmentResponsibleUser -> {
            User user = userExposeService.findById(departmentResponsibleUser.getUserId());
            ResponsibleUserVo responsibleUserVo = new ResponsibleUserVo();
            if (Objects.nonNull(user)){
                responsibleUserVo.setName(user.getName());
                responsibleUserVo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                returnList.add(responsibleUserVo);
            }
        });
        return returnList;
    }
}
