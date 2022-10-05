package com.lion.device.service.cctv;

import com.lion.core.service.BaseService;
import com.lion.device.entity.cctv.Cctv;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:35
 */
public interface CctvService extends BaseService<Cctv> {

    /**
     * 获取所有数据的id
     * @return
     */
    public List<Long> allId();


    public void importCctv(StandardMultipartHttpServletRequest multipartHttpServletRequest)throws IOException;
}
