import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ubo.iptv.manage.ManageApplication;
import com.ubo.iptv.mybatis.LogBody;
import com.ubo.iptv.mybatis.gzdp.entity.TContentDO;
import com.ubo.iptv.mybatis.gzdp.entity.TpUserDO;
import com.ubo.iptv.mybatis.gzdp.mapper.TContentMapper;
import com.ubo.iptv.mybatis.gzdp.mapper.TpUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.List;

/**
 * @Author SHAWN LIAO
 * @ClassName Test
 * @Date 2021/3/24 13:30
 * @Description 1
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ManageApplication.class})
public class Test {
    @Resource
    private TContentMapper contentMapper;
    @Resource
    private TpUserMapper userMapper;

    @org.junit.Test
    public void test() {

    }
}
