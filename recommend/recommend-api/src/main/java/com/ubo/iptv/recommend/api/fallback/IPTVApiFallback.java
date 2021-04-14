package com.ubo.iptv.recommend.api.fallback;

import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.recommend.api.IPTVApi;
import com.ubo.iptv.recommend.response.IPTVMediaVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Component
public class IPTVApiFallback implements FallbackFactory<IPTVApi> {

    @Override
    public IPTVApi create(Throwable throwable) {
        return new IPTVApi() {
            @Override
            public CommonResponse<List<IPTVMediaVO>> recommend(String sysId, String userId, Integer sceneId, Integer size, Integer categoryId, String mediaId, String pageUrl) {
                return CommonResponse.FALL_BACK;
            }
        };
    }
}
