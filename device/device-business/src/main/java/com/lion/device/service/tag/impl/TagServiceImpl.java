package com.lion.device.service.tag.impl;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.*;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.TagUseState;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagAssets;
import com.lion.device.entity.tag.dto.AddTagDto;
import com.lion.device.entity.tag.dto.UpdateTagDto;
import com.lion.device.service.tag.TagService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    @Override
    public void add(AddTagDto addTagDto) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(addTagDto,tag);
        assertDepartmentExist(addTagDto.getDepartmentId());
        assertDeviceCodeExist(tag.getDeviceCode(),null);
        assertDeviceNameExist(tag.getDeviceName(),null);
        assertTagCodeExist(tag.getTagCode(),null);
        save(tag);
    }

    @Override
    public void update(UpdateTagDto updateTagDto) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(updateTagDto,tag);
        assertDepartmentExist(updateTagDto.getDepartmentId());
        assertDeviceCodeExist(tag.getDeviceCode(),tag.getId());
        assertDeviceNameExist(tag.getDeviceName(),tag.getId());
        assertTagCodeExist(tag.getTagCode(),tag.getId());
        update(tag);
    }

    @Override
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            this.deleteById(deleteDto.getId());
            tagAssetsDao.deleteByTagId(deleteDto.getId());
            tagPatientDao.deleteByTagId(deleteDto.getId());
            tagPostdocsDao.deleteByTagId(deleteDto.getId());
            tagUserDao.deleteByTagId(deleteDto.getId());
        });
    }

    private void assertDeviceCodeExist(String deviceCode, Long id) {
        Tag tag = tagDao.findFirstByDeviceCode(deviceCode);
        if (Objects.isNull(id) && Objects.nonNull(tag) ){
            BusinessException.throwException("该设备编码已存在");
        }
        if (Objects.nonNull(id) && Objects.nonNull(tag) && !tag.getId().equals(id)){
            BusinessException.throwException("该设备编码已存在");
        }
    }

    private void assertDeviceNameExist(String deviceName, Long id) {
        Tag tag = tagDao.findFirstByDeviceName(deviceName);
        if (Objects.isNull(id) && Objects.nonNull(tag) ){
            BusinessException.throwException("该设备名称已存在");
        }
        if (Objects.nonNull(id) && Objects.nonNull(tag) && !tag.getId().equals(id)){
            BusinessException.throwException("该设备名称已存在");
        }
    }

    private void assertTagCodeExist(String tagCode, Long id) {
        Tag tag = tagDao.findFirstByTagCode(tagCode);
        if (Objects.isNull(id) && Objects.nonNull(tag) ){
            BusinessException.throwException("该标签编码已存在");
        }
        if (Objects.nonNull(id) && Objects.nonNull(tag) && !tag.getId().equals(id)){
            BusinessException.throwException("该标签编码已存在");
        }
    }

    private void assertDepartmentExist(Long departmentId) {
        Department department = departmentExposeService.findById(departmentId);
        if (Objects.isNull(department) ){
            BusinessException.throwException("该科室不存在");
        }
    }
}
