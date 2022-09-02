package com.lion.person.dao.person;

import com.lion.person.entity.person.TemporaryPerson;

import java.util.List;

public interface TemporaryPersonDaoEx {

    public List<TemporaryPerson> find(Long departmentId, String keyword, List<Long> ids);
}
