//import com.alibaba.fastjson.JSONArray;
//import com.ubo.iptv.manage.ManageApplication;
//import com.ubo.iptv.manage.service.RedisService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @Author: xuning
// * @Date: 2020-08-12
// */
//@SpringBootTest(classes = {ManageApplication.class})
//@RunWith(SpringRunner.class)
//public class TestRunner {
//    @Autowired
//    RedisService redisUtil;
//
//    @Test
//    public void demo() {
////        Map map = new HashMap<>();
////        map.put("key1","value1");
////        map.put("key2","value2");
////        redisUtil.set("codeBoot", JSONObject.toJSON(map).toString());
////
////        String codeBoot = redisUtil.get("codeBoot");
////        Map js = (Map)JSONObject.parseObject(codeBoot,HashMap.class);
////        System.out.println(codeBoot);
//
////
////        MediaKindTopDTO kindTop1 = new MediaKindTopDTO();
////        kindTop1.setMediaKind(1);
////        kindTop1.setWeight(BigDecimal.ONE);
////        MediaKindTopDTO kindTop2 = new MediaKindTopDTO();
////        kindTop2.setMediaKind(2);
////        kindTop2.setWeight(new BigDecimal(2));
////        MediaKindTopDTO kindTop3 = new MediaKindTopDTO();
////        kindTop3.setMediaKind(3);
////        kindTop3.setWeight(new BigDecimal(3));
////        List<MediaKindTopDTO> mediaKindList1 = new ArrayList<>();
////        mediaKindList1.add(kindTop2);
////        mediaKindList1.add(kindTop3);
////        mediaKindList1.add(kindTop1);
////        List<MediaKindTopDTO> mediaKindList2 = new ArrayList<>();
////        mediaKindList2.add(kindTop1);
////        mediaKindList2.add(kindTop2);
////        mediaKindList2.add(kindTop3);
////
////        MediaTypeTopDTO mediaTypeTop1 = new MediaTypeTopDTO();
////        mediaTypeTop1.setMediaType(11);
////        mediaTypeTop1.setMediaKindList(mediaKindList1);
////        MediaTypeTopDTO mediaTypeTop2 = new MediaTypeTopDTO();
////        mediaTypeTop2.setMediaType(22);
////        mediaTypeTop2.setMediaKindList(mediaKindList2);
////
////        List<MediaTypeTopDTO> mediaTypeTopList = new ArrayList<>();
////        mediaTypeTopList.add(mediaTypeTop2);
////        mediaTypeTopList.add(mediaTypeTop1);
////        redisUtil.set("codeBoot", JSONObject.toJSON(mediaTypeTopList).toString());
//
//        String result = redisUtil.get("codeBoot");
//        JSONArray ja = JSONArray.parseArray(result);
//    }
//
//}
