package com.lion.device.service.tag.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.*;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.tag.*;
import com.lion.device.entity.tag.dto.AddTagDto;
import com.lion.device.entity.tag.dto.UpdateTagDto;
import com.lion.device.entity.tag.vo.ListTagVo;
import com.lion.device.service.tag.TagService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.department.DepartmentUser;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
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
 * @date 2021/4/7下午8:16
 */
@Service
public class TagServiceImpl extends BaseServiceImpl<Tag> implements TagService {

    @Autowired
    private TagDao tagDao;

    @Autowired
    private TagAssetsDao tagAssetsDao;

    @Autowired
    private TagUserDao tagUserDao;

    @Autowired
    private TagPatientDao tagPatientDao;

    @Autowired
    private TagPostdocsDao tagPostdocsDao;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @DubboReference
    private UserExposeService userExposeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void add(AddTagDto addTagDto) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(addTagDto,tag);
        assertDepartmentExist(addTagDto.getDepartmentId());
        assertDeviceCodeExist(tag.getDeviceCode(),null);
        assertDeviceNameExist(tag.getDeviceName(),null);
        assertTagCodeExist(tag.getTagCode(),null);
        assertTagPurpose(tag);
        tag = save(tag);
        redisTemplate.opsForValue().set(RedisConstants.TAG+tag.getId(),tag, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.TAG_CODE+tag.getTagCode(),tag, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    public void update(UpdateTagDto updateTagDto) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(updateTagDto,tag);
        assertDepartmentExist(updateTagDto.getDepartmentId());
        assertDeviceCodeExist(tag.getDeviceCode(),tag.getId());
        assertDeviceNameExist(tag.getDeviceName(),tag.getId());
        assertTagCodeExist(tag.getTagCode(),tag.getId());
        assertTagPurpose(tag);
        update(tag);
        redisTemplate.opsForValue().set(RedisConstants.TAG+tag.getId(),tag, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.TAG_CODE+tag.getTagCode(),tag, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    public void delete(List<DeleteDto> deleteDtoList) {

        deleteDtoList.forEach(deleteDto -> {
            TagUser tagUser = tagUserDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            if (Objects.nonNull(tagUser)) {
                Tag tag = findById(tagUser.getTagId());
                BusinessException.throwException(tag.getDeviceName() + "与用户绑定不能删除");
            }
            TagAssets tagAssets = tagAssetsDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            if (Objects.nonNull(tagUser)) {
                Tag tag = findById(tagAssets.getTagId());
                BusinessException.throwException(tag.getDeviceName() + "与资产绑定不能删除");
            }
            TagPatient tagPatient = tagPatientDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            if (Objects.nonNull(tagPatient)) {
                Tag tag = findById(tagPatient.getTagId());
                BusinessException.throwException(tag.getDeviceName() + "与患者绑定不能删除");
            }
            TagPostdocs tagPostdocs = tagPostdocsDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            if (Objects.nonNull(tagPostdocs)) {
                Tag tag = findById(tagPostdocs.getTagId());
                BusinessException.throwException(tag.getDeviceName() + "与流动人员绑定不能删除");
            }
        });

        deleteDtoList.forEach(deleteDto -> {
            TagUser tagUser = tagUserDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            this.deleteById(deleteDto.getId());
            tagAssetsDao.deleteByTagId(deleteDto.getId());
            tagPatientDao.deleteByTagId(deleteDto.getId());
            tagPostdocsDao.deleteByTagId(deleteDto.getId());
            tagUserDao.deleteByTagId(deleteDto.getId());
            redisTemplate.delete(RedisConstants.USER_TAG+tagUser.getUserId());
            redisTemplate.delete(RedisConstants.TAG_USER+tagUser.getTagId());
        });
    }

    @Override
    public IPageResultData<List<ListTagVo>> list(Integer battery, String tagCode, TagType type, TagPurpose purpose, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(tagCode)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_tagCode",tagCode);
        }
        if (Objects.nonNull(type)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_type",type);
        }
        if (Objects.nonNull(purpose)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_purpose",purpose);
        }
        if (Objects.nonNull(battery)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_battery",battery);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        Page<Tag> page = findNavigator(lionPage);
        List<Tag> list = page.getContent();
        List<ListTagVo> returnList = new ArrayList<>();
        list.forEach(tag->{
            ListTagVo vo = new ListTagVo();
            BeanUtils.copyProperties(tag,vo);
            if (Objects.equals(tag.getPurpose(),TagPurpose.STAFF)) {
                TagUser tagUser = tagUserDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
                if (Objects.nonNull(tagUser)) {
                    User user = userExposeService.findById(tagUser.getUserId());
                    if (Objects.nonNull(user)) {
                        vo.setBindingName(user.getName()+":"+user.getNumber());
                        Department department = departmentUserExposeService.findDepartment(user.getId());
                        if (Objects.nonNull(department)) {
                            vo.setDepartmentName(department.getName());
                        }
                    }
                }
            }else if (Objects.equals(tag.getPurpose(),TagPurpose.PATIENT)) {

            }else if (Objects.equals(tag.getPurpose(),TagPurpose.POSTDOCS)) {

            }else if (Objects.equals(tag.getPurpose(),TagPurpose.ASSETS)) {
                TagAssets tagAssets = tagAssetsDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
                if (Objects.nonNull(tagAssets)){

                }
            }
            returnList.add(vo);
        });
        return new PageResultData<List<ListTagVo>>(returnList,page.getPageable(),page.getTotalElements());
    }

    private void assertDeviceCodeExist(String deviceCode, Long id) {
        Tag tag = tagDao.findFirstByDeviceCode(deviceCode);
        if ((Objects.isNull(id) && Objects.nonNull(tag)) || (Objects.nonNull(id) && Objects.nonNull(tag) && !Objects.equals(tag.getId(),id) )){
            BusinessException.throwException("该设备编码已存在");
        }
    }

    private void assertDeviceNameExist(String deviceName, Long id) {
        Tag tag = tagDao.findFirstByDeviceName(deviceName);
        if ((Objects.isNull(id) && Objects.nonNull(tag) ) || (Objects.nonNull(id) && Objects.nonNull(tag) && !Objects.equals(tag.getId(),id))){
            BusinessException.throwException("该设备名称已存在");
        }
    }

    private void assertTagCodeExist(String tagCode, Long id) {
        Tag tag = tagDao.findFirstByTagCode(tagCode);
        if ((Objects.isNull(id) && Objects.nonNull(tag)) || (Objects.nonNull(id) && Objects.nonNull(tag) && !Objects.equals(tag.getId(),id)) ){
            BusinessException.throwException("该标签编码已存在");
        }
    }

    private void assertDepartmentExist(Long departmentId) {
        Department department = departmentExposeService.findById(departmentId);
        if (Objects.isNull(department) ){
            BusinessException.throwException("该科室不存在");
        }
    }

    private void assertTagPurpose(Tag tag) {
        if (Objects.equals(tag.getType(), TagType.STAFF)) {
            if (!Objects.equals(tag.getPurpose(), TagPurpose.STAFF)) {
                BusinessException.throwException("该标签分类只能用途于员工");
            }
        }

        if (Objects.equals(tag.getType(), TagType.TEMPERATURE_HUMIDITY)) {
            if (!Objects.equals(tag.getPurpose(), TagPurpose.THERMOHYGROGRAPH)) {
                BusinessException.throwException("该标签分类只能用途于温湿仪");
            }
        }
    }
}
