package com.lion.manage.service.rule.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.dao.rule.WashDeviceTypeDao;
import com.lion.manage.dao.rule.WashTemplateDao;
import com.lion.manage.dao.rule.WashTemplateItemDao;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.rule.WashDeviceType;
import com.lion.manage.entity.rule.WashTemplate;
import com.lion.manage.entity.rule.WashTemplateItem;
import com.lion.manage.entity.rule.dto.AddWashTemplateDto;
import com.lion.manage.entity.rule.dto.UpdateWashTemplateDto;
import com.lion.manage.entity.rule.vo.DetailsWashTemplateVo;
import com.lion.manage.entity.rule.vo.ListWashTemplateItemVo;
import com.lion.manage.entity.rule.vo.ListWashTemplateVo;
import com.lion.manage.service.rule.WashTemplateItemService;
import com.lion.manage.service.rule.WashTemplateService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.AssertUtil;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午10:53
 */
@Service
public class WashTemplateServiceImpl extends BaseServiceImpl<WashTemplate> implements WashTemplateService {

    @Autowired
    private WashTemplateDao washTemplateDao;

    @Autowired
    private WashTemplateItemService washTemplateItemService;

    @Autowired
    private WashTemplateItemDao washTemplateItemDao;

    @Autowired
    private RegionDao regionDao;

    @Autowired
    private WashDeviceTypeDao washDeviceTypeDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Override
    @Transactional
    public WashTemplate add(AddWashTemplateDto addWashTemplateDto) {
        WashTemplate washTemplate = new WashTemplate();
        BeanUtils.copyProperties(addWashTemplateDto,washTemplate);
        assertNameExist(washTemplate.getName(),null);
        washTemplate = save(washTemplate);
        washTemplateItemService.add(addWashTemplateDto.getWashTemplateItems(),washTemplate.getId());
        persistence2Redis(details(washTemplate.getId()),washTemplate.getId(),false);
        return washTemplate;
    }

    @Override
    @Transactional
    public void update(UpdateWashTemplateDto updateWashTemplateDto) {
        WashTemplate washTemplate = new WashTemplate();
        BeanUtils.copyProperties(updateWashTemplateDto,washTemplate);
        assertNameExist(washTemplate.getName(),washTemplate.getId());
        update(washTemplate);
        washTemplateItemService.add(updateWashTemplateDto.getWashTemplateItems(),washTemplate.getId());
        persistence2Redis(details(washTemplate.getId()),washTemplate.getId(),false);
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDto) {
        deleteDto.forEach(dto->{
            WashTemplate washTemplate = this.findById(dto.getId());
            if (Objects.nonNull(washTemplate)) {
                AssertUtil.isTrue(regionDao.countByWashTemplateId(dto.getId()) > 0, MessageI18nUtil.getMessage("2000104",new Object[]{washTemplate.getName()}));
            }
        });
        deleteDto.forEach(dto->{
            this.deleteById(dto.getId());
            persistence2Redis(null,dto.getId(),true);
        });
    }

    @Override
    public IPageResultData<List<ListWashTemplateVo>> list(String name, LionPage LionPage) {
        Map<String,Object> searchParameter = new HashMap<String,Object>();
        if (StringUtils.hasText(name)) {
            searchParameter.put(SearchConstant.LIKE + "_assetsTypeName", name);
        }
        Page<WashTemplate> page = findNavigator(LionPage,searchParameter);
        List<WashTemplate> list = page.getContent();
        List<ListWashTemplateVo> returnList = new ArrayList<ListWashTemplateVo>();
        list.forEach(washTemplate -> {
            ListWashTemplateVo listWashTemplateVo = new ListWashTemplateVo();
            DetailsWashTemplateVo detailsWashTemplateVo = details(washTemplate.getId());
            BeanUtils.copyProperties(detailsWashTemplateVo,listWashTemplateVo);
            User user = userExposeService.findById(listWashTemplateVo.getCreateUserId());
            if (Objects.nonNull(user)) {
                listWashTemplateVo.setCreateUserName(user.getName());
                listWashTemplateVo.setCreateUserHeadPortrait(user.getHeadPortrait());
                listWashTemplateVo.setCreateUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            returnList.add(listWashTemplateVo);
        });
        return new PageResultData<List<ListWashTemplateVo>>(returnList,LionPage,page.getTotalElements());
    }

    @Override
    public DetailsWashTemplateVo details(Long id) {
        WashTemplate washTemplate = this.findById(id);
        if (Objects.isNull(washTemplate)) {
            return null;
        }
        DetailsWashTemplateVo detailsWashTemplateVo = new DetailsWashTemplateVo();
        BeanUtils.copyProperties(washTemplate,detailsWashTemplateVo);
        List<WashTemplateItem> list = washTemplateItemDao.findByWashTemplateId(washTemplate.getId());
        List<ListWashTemplateItemVo> listWashTemplateItemVos = new ArrayList<ListWashTemplateItemVo>();
        list.forEach(washTemplateItem -> {
            ListWashTemplateItemVo listWashTemplateItemVo = new ListWashTemplateItemVo();
            BeanUtils.copyProperties(washTemplateItem,listWashTemplateItemVo);
            List<WashDeviceType> washDeviceTypes = washDeviceTypeDao.findByWashId(washTemplateItem.getId());
            List<com.lion.manage.entity.enums.WashDeviceType> deviceTypes = new ArrayList<com.lion.manage.entity.enums.WashDeviceType>();
            washDeviceTypes.forEach(washDeviceType -> {
                deviceTypes.add(washDeviceType.getType());
            });
            listWashTemplateItemVo.setWashDeviceTypes(deviceTypes);
            listWashTemplateItemVos.add(listWashTemplateItemVo);
        });
        User user = userExposeService.findById(washTemplate.getCreateUserId());
        if (Objects.nonNull(user)) {
            detailsWashTemplateVo.setCreateUserName(user.getName());
        }
        detailsWashTemplateVo.setListWashTemplateItemVos(listWashTemplateItemVos);
        return detailsWashTemplateVo;
    }

    private void persistence2Redis(DetailsWashTemplateVo detailsWashTemplateVo,Long id, Boolean isDelete){
        if (Objects.equals(isDelete,true)) {
            redisTemplate.delete(id);
            return;
        }
        redisTemplate.opsForValue().set(RedisConstants.WASH_TEMPLATE+ id,detailsWashTemplateVo,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    private void assertNameExist(String name, Long id) {
        WashTemplate washTemplate = washTemplateDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(washTemplate)) || (Objects.nonNull(id) && Objects.nonNull(washTemplate) && !Objects.equals(washTemplate.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000073"));
        }
    }
}
