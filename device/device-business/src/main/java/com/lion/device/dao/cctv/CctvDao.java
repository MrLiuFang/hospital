package com.lion.device.dao.cctv;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.cctv.Cctv;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:24
 */
public interface CctvDao extends BaseDao<Cctv> {

    /**
     * 根据id数组查询
     * @param ids
     * @return
     */
    public List<Cctv> findByIdIn(List<Long> ids);

    /**
     * 统计科室内的cctv数量
     * @param departmentId
     * @return
     */
    public int countByDepartmentId(Long departmentId);

    /**
     * 根据编码查询
     * @param code
     * @return
     */
    public Cctv findFirstByCode(String code);

    @Query( " select t.id from Cctv t ")
    public List<Long> allId();

    public List<Cctv> findByRegionId(Long regionId);

    public List<Cctv> findByDepartmentId(Long departmentId);

}
