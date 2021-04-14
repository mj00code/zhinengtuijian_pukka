package cn.pukkasoft.datasync.handler;

import cn.pukkasoft.datasync.service.impl.CanalClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author SHAWN LIAO
 * @ClassName CanalJobHandler
 * @Date 2020/12/10 11:24
 * @Description canal处理增量数据
 */

@Service
@JobHandler(value = "canalSyncJobHandler")
@Slf4j
public class CanalSyncJobHandler extends IJobHandler {

    @Autowired
    private CanalClient canalClient;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("======Start process task=======");
        canalClient.init();
        return SUCCESS;
    }
}
