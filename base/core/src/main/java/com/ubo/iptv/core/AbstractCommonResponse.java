package com.ubo.iptv.core;

import com.ubo.iptv.core.enums.CommonCodeEnum;

import java.io.Serializable;

/**
 * 此为预留扩展点，比如需要对返回数据进行脱敏处理等等，在这里注入脱敏规则可以对所有返回对象生效。
 * Created by zhouyu on 2017/9/30.
 */
public abstract class AbstractCommonResponse<T> implements Serializable {

    public abstract int getResult();

    public abstract int getStatus();

    /**
     * 判断操作是否是根本成功
     *
     * @return
     */
    public boolean _isCompleteOk() {
        return (this.getResult() == CommonCodeEnum.GLOBAL_SUCCESS.result() && this.getStatus() == CommonCodeEnum.GLOBAL_SUCCESS.status());
    }

    /**
     * 判断操作是否成功
     *
     * @return
     */
    public boolean _isOk() {
        return this.getResult() == CommonCodeEnum.GLOBAL_SUCCESS.result();
    }

    /**
     * 判断操作是否失败
     *
     * @return
     */
    public boolean _isFailed() {
        return !this._isOk();
    }

}
