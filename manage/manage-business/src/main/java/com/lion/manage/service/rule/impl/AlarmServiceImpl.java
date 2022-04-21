package com.lion.manage.service.rule.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.ResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.rule.AlarmDao;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.rule.Alarm;
import com.lion.manage.entity.rule.dto.AddAlarmDto;
import com.lion.manage.entity.rule.dto.UpdateAlarmDto;
import com.lion.manage.entity.rule.vo.DetailsAlarmVo;
import com.lion.manage.entity.rule.vo.ListAlarmVo;
import com.lion.manage.service.rule.AlarmService;
import com.lion.manage.service.rule.AlarmUserService;
import com.lion.manage.service.rule.AlarmWayService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.lion.core.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/13下午1:37
 */
@Service
public class AlarmServiceImpl extends BaseServiceImpl<Alarm> implements AlarmService {

    @Autowired
    private AlarmDao alarmDao;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Autowired
    private AlarmWayService alarmWayService;

    @Autowired
    private AlarmUserService alarmUserService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void add(AddAlarmDto addAlarmDto) {
        Alarm alarm = new Alarm();
        BeanUtils.copyProperties(addAlarmDto,alarm);
        alarmClassify(alarm);
        assertCodeExist(addAlarmDto.getCode(),alarm.getClassify(), alarm.getLevel(), null);
        assertAlarmClassifytExist(alarm.getClassify(),alarm.getContent(),alarm.getLevel(),null);
        alarm = save(alarm);
        alarmWayService.add(alarm.getId(),addAlarmDto.getWays());
        alarmUserService.add(alarm.getId(),addAlarmDto.getUserIds());
        persistenceRedis(alarm,false);
    }

    @Override
    @Transactional
    public void update(UpdateAlarmDto updateAlarmDto) {
        Alarm alarm = new Alarm();
        BeanUtils.copyProperties(updateAlarmDto,alarm);
        alarmClassify(alarm);
        assertCodeExist(alarm.getCode(),alarm.getClassify(),alarm.getLevel() , alarm.getId());
        assertAlarmClassifytExist(alarm.getClassify(),alarm.getContent(),alarm.getLevel(),alarm.getId());
        update(alarm);
        alarmWayService.add(alarm.getId(),updateAlarmDto.getWays());
        alarmUserService.add(alarm.getId(),updateAlarmDto.getUserIds());
        persistenceRedis(alarm,false);
    }

    @Override
    public DetailsAlarmVo details(Long id) {
        com.lion.core.Optional<Alarm> optional = findById(id);
        if (optional.isPresent()){
            Alarm alarm = optional.get();
            DetailsAlarmVo detailsAlarmVo = new DetailsAlarmVo();
            BeanUtils.copyProperties(alarm,detailsAlarmVo);
            detailsAlarmVo.setManagerVos(convertManagerVo(detailsAlarmVo.getManager()));
            detailsAlarmVo.setWays(alarmWayService.find(alarm.getId()));
            return detailsAlarmVo;
        }
        return null;
    }

    @Override
    public IPageResultData<List<ListAlarmVo>> list(String content, AlarmClassify classify, Integer level, LionPage lionPage) {
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(content)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_content",content);
        }
        if (Objects.nonNull(classify)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_classify",classify);
        }
        if (Objects.nonNull(level)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_level",level);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<Alarm> page = findNavigator(lionPage);
        List<Alarm> list = page.getContent();
        List<ListAlarmVo> returnList = new ArrayList<>();
        list.forEach(alarm -> {
            ListAlarmVo vo = new ListAlarmVo();
            BeanUtils.copyProperties(alarm,vo);
            vo.setManagerVos(convertManagerVo(alarm.getManager()));
            vo.setWays(alarmWayService.find(alarm.getId()));
            returnList.add(vo);
        });

        return new PageResultData(returnList,page.getPageable(),page.getTotalElements());
    }

    @Override
    public void delete(List<DeleteDto> deleteDtos) {
        deleteDtos.forEach(deleteDto -> {
            com.lion.core.Optional<Alarm> optional = findById(deleteDto.getId());
            if (optional.isPresent()) {
                persistenceRedis(optional.get(), true);
            }
            deleteById(deleteDto.getId());
        });
    }

    @Override
    public List<Alarm> userAlarm(Long userId) {
        return alarmDao.findByUserId(userId);
    }

    private void persistenceRedis(Alarm alarm,Boolean delete){
        if (Objects.equals(true,delete)){
            redisTemplate.delete(RedisConstants.ALARM+alarm.getId());
            if (Objects.isNull(alarm.getLevel())){
                redisTemplate.delete(RedisConstants.ALARM_CLASSIFY_CODE+alarm.getClassify().toString()+alarm.getCode().getKey());
            }else {
                redisTemplate.delete(RedisConstants.ALARM_CLASSIFY_CODE+alarm.getClassify().toString()+alarm.getCode().getKey()+alarm.getLevel());
            }
        }else {
            redisTemplate.opsForValue().set(RedisConstants.ALARM+alarm.getId(),alarm, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            if (Objects.isNull(alarm.getLevel())){
                redisTemplate.opsForValue().set(RedisConstants.ALARM_CLASSIFY_CODE+alarm.getClassify().toString()+alarm.getCode().getKey(),alarm.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }else {
                redisTemplate.opsForValue().set(RedisConstants.ALARM_CLASSIFY_CODE+alarm.getClassify().toString()+alarm.getCode().getKey()+alarm.getLevel(),alarm.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
    }

    private List<DetailsAlarmVo.ManagerVo> convertManagerVo(String manger){
        if (StringUtils.hasText(manger)){
            String ids[] = manger.split(",");
            List<DetailsAlarmVo.ManagerVo> list = new ArrayList<>();
            for (String userId : ids){
                com.lion.core.Optional<User> optional = userExposeService.findById(Long.valueOf(userId));
                if (optional.isPresent()){
                    User user = optional.get();
                    DetailsAlarmVo.ManagerVo vo = new DetailsAlarmVo.ManagerVo();
                    vo.setName(user.getName());
                    vo.setId(user.getId());
                    vo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                    list.add(vo);
                }
            }
            return list;
        }
        return null;
    }

    private void alarmClassify(Alarm alarm){
        if (Objects.equals(alarm.getClassify(),AlarmClassify.PATIENT)){
            if (Objects.isNull(alarm.getLevel())) {
                BusinessException.throwException(MessageI18nUtil.getMessage("2000083"));
            }
        }
    }

    private void assertCodeExist(SystemAlarmType code,AlarmClassify classify,Integer level, Long id) {
//        Alarm alarm = null;
//        if (Objects.nonNull(level)) {
//            alarm = alarmDao.findFirstByCodeAndClassifyAndLevel(code,classify,level);
//        }else {
//            alarm = alarmDao.findFirstByCodeAndClassify(code,classify);
//        }
//        if ((Objects.isNull(id) && Objects.nonNull(alarm)) || (Objects.nonNull(id) && Objects.nonNull(alarm) && !Objects.equals(alarm.getId(),id)) ){
//            BusinessException.throwException(MessageI18nUtil.getMessage("2000084"));
//        }
    }

    private void assertAlarmClassifytExist(AlarmClassify classify,String content, Integer level, Long id) {
//        Alarm alarm = null;
//        if (Objects.equals(classify,AlarmClassify.PATIENT)){
//            alarm = alarmDao.findFirstByClassifyAndLevelAndContent(classify,level,content);
//        }else {
//            alarm = alarmDao.findFirstByClassifyAndContent(classify,content);
//        }
//        if ((Objects.isNull(id) && Objects.nonNull(alarm)) || (Objects.nonNull(id) && Objects.nonNull(alarm) && !Objects.equals(alarm.getId(),id)) ){
//            BusinessException.throwException(MessageI18nUtil.getMessage("2000085",new String[]{content}));
//        }
    }

}
