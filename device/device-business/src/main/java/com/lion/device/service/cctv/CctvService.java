package com.lion.device.service.cctv;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.cctv.vo.CctvVo;
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

    public CctvVo convertVo(Cctv cctv);

    public void importCctv(StandardMultipartHttpServletRequest multipartHttpServletRequest)throws IOException;

    public void export( String regionId,  String name, String cctvId, Boolean isOnline, String ids, LionPage lionPage) throws IOException, IllegalAccessException;

    public IPageResultData<List<CctvVo>> list(String regionId,  String name,  String cctvId, Boolean isOnline,String ids,  LionPage lionPage);
}
