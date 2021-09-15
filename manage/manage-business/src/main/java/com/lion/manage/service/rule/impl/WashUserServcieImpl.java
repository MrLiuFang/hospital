package com.lion.manage.service.rule.impl;
//
//import com.lion.core.service.impl.BaseServiceImpl;
//import com.lion.exception.BusinessException;
//import com.lion.manage.dao.rule.WashUserDao;
//import com.lion.manage.entity.enums.WashRuleType;
//import com.lion.manage.entity.rule.Wash;
//import com.lion.manage.entity.rule.WashUser;
//import com.lion.manage.service.rule.WashUserServcie;
//import com.lion.upms.entity.user.User;
//import com.lion.upms.expose.user.UserExposeService;
//import com.lion.utils.MessageI18nUtil;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.springframework.beans.Mergeable;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Objects;
//import java.util.TreeMap;
//
///**
// * @author Mr.Liu
// * @Description:
// * @date 2021/4/9下午5:01
// */
//@Service
//public class WashUserServcieImpl extends BaseServiceImpl<WashUser> implements WashUserServcie {
//
//    @Autowired
//    private WashUserDao washUserDao;
//
//    @DubboReference
//    private UserExposeService userExposeService ;
//
//    @Override
//    public void add(List<Long> userId, Wash wash) {
//        if (Objects.nonNull(wash)){
//            washUserDao.deleteByWashId(wash.getId());
//        }
//        userId.forEach(id->{
//            if (Objects.equals(wash.getType(),WashRuleType.LOOP) && Objects.equals(false,wash.getIsAllUser())) {
//                List<WashUser> list = washUserDao.find(id, WashRuleType.LOOP, wash.getId());
//                if (Objects.nonNull(list) && list.size() > 0) {
//                    User user = userExposeService.findById(id);
//                    BusinessException.throwException(user.getName() + MessageI18nUtil.getMessage("2000094"));
//                }
//            }
//        });
//        userId.forEach(id->{
//            WashUser washUser = new WashUser();
//            washUser.setUserId(id);
//            washUser.setWashId(wash.getId());
//            save(washUser);
//        });
//    }
//
//    @Override
//    public int delete(Long washId) {
//        return washUserDao.deleteByWashId(washId);
//    }
//
//    @Override
//    public List<WashUser> find(Long washId) {
//        List<WashUser> list = washUserDao.findByWashId(washId);
//        return list;
//    }
//}
