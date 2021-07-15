package com.lion.upms.expose.tcc;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/7/15 下午8:18
 */
@LocalTCC
public interface UserTccExposeService {

    @TwoPhaseBusinessAction(name = "tryBusiness", commitMethod = "commitTcc", rollbackMethod = "cancelTcc")
    public void tryBusiness(@BusinessActionContextParameter(paramName = "id") String id);

    public void commitTcc(BusinessActionContext context);

    public void cancelTcc(BusinessActionContext context);
}
