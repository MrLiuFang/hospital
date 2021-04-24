package com.lion.manage.service.rule.impl;

import com.lion.common.ResdisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.ResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.BaseService;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.rule.AlarmDao;
import com.lion.manage.entity.enums.AlarmClassify;
import com.lion.manage.entity.rule.Alarm;
import com.lion.manage.entity.rule.Wash;
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
    private RedisTemplate<String,Alarm> redisTemplate;

    @Override
    public void add(AddAlarmDto addAlarmDto) {
        Alarm alarm = new Alarm();
        BeanUtils.copyProperties(addAlarmDto,alarm);
        assertContentExist(alarm.getContent(),alarm.getClassify(),null);
        alarm = save(alarm);
        redisTemplate.opsForValue().set(ResdisConstants.ALARM+alarm.getId(),alarm);
    }

    @Override
    public void update(UpdateAlarmDto updateAlarmDto) {
        Alarm alarm = new Alarm();
        BeanUtils.copyProperties(updateAlarmDto,alarm);
        assertContentExist(alarm.getContent(),alarm.getClassify(),alarm.getId());
        update(alarm);
        redisTemplate.opsForValue().set(ResdisConstants.ALARM+alarm.getId(),alarm);
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
            deleteById(deleteDto.getId());
            redisTemplate.delete(ResdisConstants.ALARM+deleteDto.getId());
        });
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
