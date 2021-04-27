package com.lion.manage.service.rule.impl;

import com.lion.common.RedisConstants;
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
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private RedisTemplate redisTemplate;

    @Override
    public void add(AddAlarmDto addAlarmDto) {
        Alarm alarm = new Alarm();
        BeanUtils.copyProperties(addAlarmDto,alarm);
        if (Objects.equals(alarm.getClassify(),AlarmClassify.PATIENT)){
            if (Objects.isNull(alarm.getLevel())) {
                BusinessException.throwException("级别不能为空");
            }
        }
        assertAlarmClassifytExist(alarm.getClassify(),alarm.getLevel(),null);
        alarm = save(alarm);
        persistenceRedis(alarm,false);
    }

    @Override
    public void update(UpdateAlarmDto updateAlarmDto) {
        Alarm alarm = new Alarm();
        BeanUtils.copyProperties(updateAlarmDto,alarm);
        if (Objects.equals(alarm.getClassify(),AlarmClassify.PATIENT)){
            if (Objects.isNull(alarm.getLevel())) {
                BusinessException.throwException("级别不能为空");
            }
        }
        assertAlarmClassifytExist(alarm.getClassify(),alarm.getLevel(),alarm.getId());
        update(alarm);
        persistenceRedis(alarm,false);
    }

    @Override
    public DetailsAlarmVo details(Long id) {
        Alarm alarm = findById(id);
        if (Objects.nonNull(alarm)){
            DetailsAlarmVo detailsAlarmVo = new DetailsAlarmVo();
            BeanUtils.copyProperties(alarm,detailsAlarmVo);
            detailsAlarmVo.setManagerVos(convertManagerVo(detailsAlarmVo.getManager()));
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
        lionPage.setJpqlParameter(jpqlParameter);
        Page<Alarm> page = findNavigator(lionPage);
        List<Alarm> list = page.getContent();
        List<ListAlarmVo> renturnList = new ArrayList<>();
        list.forEach(alarm -> {
            ListAlarmVo vo = new ListAlarmVo();
            BeanUtils.copyProperties(alarm,vo);
            vo.setManagerVos(convertManagerVo(alarm.getManager()));
            renturnList.add(vo);
        });
        return new PageResultData(renturnList,page.getPageable(),page.getTotalElements());
    }

    @Override
    public void delete(List<DeleteDto> deleteDtos) {
        deleteDtos.forEach(deleteDto -> {
            Alarm alarm = findById(deleteDto.getId());
            deleteById(deleteDto.getId());
            persistenceRedis(alarm,true);
        });
    }

    private void persistenceRedis(Alarm alarm,Boolean delete){
        if (Objects.equals(true,delete)){
            redisTemplate.delete(RedisConstants.ALARM+alarm.getId());
            if (Objects.isNull(alarm.getLevel())){
                redisTemplate.opsForValue().set(RedisConstants.ALARM_CLASSIFY+alarm.getClassify().toString(),alarm.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }else {
                redisTemplate.opsForValue().set(RedisConstants.ALARM_CLASSIFY+alarm.getClassify().toString()+alarm.getLevel(),alarm.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }else {
            redisTemplate.opsForValue().set(RedisConstants.ALARM+alarm.getId(),alarm, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            if (Objects.isNull(alarm.getLevel())){
                redisTemplate.opsForValue().set(RedisConstants.ALARM_CLASSIFY+alarm.getClassify().toString(),alarm.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }else {
                redisTemplate.opsForValue().set(RedisConstants.ALARM_CLASSIFY+alarm.getClassify().toString()+alarm.getLevel(),alarm.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }
    }

    private List<DetailsAlarmVo.ManagerVo> convertManagerVo(String manger){
        if (StringUtils.hasText(manger)){
            String ids[] = manger.split(",");
            List<DetailsAlarmVo.ManagerVo> list = new ArrayList<>();
            for (String userId : ids){
                User user = userExposeService.findById(Long.valueOf(userId));
                if (Objects.nonNull(user)){
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

    private void assertAlarmClassifytExist(AlarmClassify classify, Integer level, Long id) {
        Alarm alarm = null;
        if (Objects.equals(classify,AlarmClassify.PATIENT)){
            alarm = alarmDao.findFirstByClassifyAndLevel(classify,level);
        }else {
            alarm = alarmDao.findFirstByClassify(classify);
        }
        if (Objects.isNull(id) && Objects.nonNull(alarm) ){
            BusinessException.throwException("该警报分类已存在，重复的分类警告会导致警告冲突");
        }
        if (Objects.nonNull(id) && Objects.nonNull(alarm) && !alarm.getId().equals(id)){
            BusinessException.throwException("该警报分类已存在，重复的分类警告会导致警告冲突");
        }
    }

    private void assertContentExist(String content, AlarmClassify classify, Long id) {
        Alarm alarm = alarmDao.findFirstByContentAndClassify(content,classify);
        if (Objects.isNull(id) && Objects.nonNull(alarm) ){
            BusinessException.throwException("该警报内容已存在");
        }
        if (Objects.nonNull(id) && Objects.nonNull(alarm) && !alarm.getId().equals(id)){
            BusinessException.throwException("该警报内容已存在");
        }
    }
}
