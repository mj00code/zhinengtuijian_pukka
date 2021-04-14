package com.ubo.iptv.job.service;

import com.ubo.iptv.core.constant.ElasticSearchConstant;
import com.ubo.iptv.core.enums.ElasticIndexEnum;
import com.ubo.iptv.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

/**
 * @Author: xuning
 * @Date: 2020-11-03
 */
@Component
@Slf4j
public class EsService {

    @Autowired
    private RestHighLevelClient esClient;


    public SearchResponse search(LocalDate date) {
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        String index = DateUtil.getEsIndexByDate(date);
        SearchRequest searchRequest = new SearchRequest(index); // 新建索引搜索请求
        searchRequest.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = boolQuery();
        boolQueryBuilder.must().add(QueryBuilders.rangeQuery("create_time").lte(date).gte(date));
        boolQueryBuilder.must().add(QueryBuilders.existsQuery("user_db_id"));
        boolQueryBuilder.must().add(QueryBuilders.existsQuery("media_id"));
        boolQueryBuilder.must().add(QueryBuilders.existsQuery("event_type"));
        boolQueryBuilder.must().add(QueryBuilders.existsQuery("create_time"));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(10000); //设定每次返回多少条数据
        searchSourceBuilder.fetchSource(new String[]{"user_db_id", "media_id", "event_type", "log_time",
                "create_time"}, null);//设置返回字段和排除字段
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            return searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es错误", e);
        }
        return null;
    }

    public SearchResponse search(String scrollId) {
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(TimeValue.timeValueMinutes(1L));

        SearchResponse searchResponse = null;
        try {
            return searchResponse = esClient.scroll(scrollRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("es错误", e);
        }
        return null;
    }

    public void alias() {
        //Enum.values();拿到枚举别名列表数组
        ElasticIndexEnum[] enums = ElasticIndexEnum.values();
        //(“start_date1”：{“2”，“3”，“4”})，(“start_date2”：{“b”，“c”，“d”}),...
        Map<String, List<String>> map = new HashMap<>();
        for (ElasticIndexEnum e : enums) {
            //①、返回此 enum 枚举定义的常量的名称，与其enum声明中声明的完全一致
            //②、获取 注解上 定义的value值
            String name = e.name();
            String value = e.value();

            LocalDate start = null;
            LocalDate end = LocalDate.now();

            //"一周内日志(包含当天)"
            if (value.equalsIgnoreCase("week")) {
                // 当前本周数据
                //即为拿到--》本周开始时间星期一 ：LocalDate 【年月日】
                start = end.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            } else {
                //过去多少天的所有数据
                //RT-》(多少天内的数据，包含当天的)  ，没有RT-》（多少天前的数据，不包含当天的）
                //比如：查询 7天前数据 ，今天 03-10 ： 则 查询的开始时间为 （03-10）- 1 = 03-09
                //                                         结束时间为 （03-09）- 6 = 03-03
                if (!name.contains("RT")) end = end.minusDays(1);   // 结束时间 = 今天时间-1
                start = end.minusDays(Integer.parseInt(value) - 1); // 开始时间 = 结束时间-（（value) - 1）
            }
            //检查此日期是否在指定日期之后。
            //将所有的正常的 start < end 的数据 加到map 集合中
            while (!start.isAfter(end)) {
                String date = DateUtil.format(start, "yyyyMMdd");
                //临时索引：template_log_index = "iptv_log_%s";
                String index = String.format(ElasticSearchConstant.template_log_index, date);
                start = start.plusDays(1);
                //根据 index  【iptv_log_start日期】,
                //查询 list   【start相同的枚举值】
                List<String> list = map.get(index);
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(index, list);  //{（iptv_log_20200309:{空对象}）}
                }
                list.add(name.toLowerCase()); //log_index_7d
                System.out.println(map.get(index));
                //不应该是 map.put(index，list)
            }
        }

        try { //{（iptv_log_20200309:{枚举}），（iptv_log_20200309:{枚举}），(iptv_log_20200309:{枚举})...}==>遍历各个开始日期对应的的 ES数据枚举
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                String index = entry.getKey();
                //RestClient-索引和文档操作
                //判断 index索引 是否存在 iptv_log_startdate[某一时间]
                GetIndexRequest existRequest = new GetIndexRequest();
                //赋值 获取 指定 index 对应的 GetIndexRequest 对象
                existRequest.indices(index);
                if (esClient.indices().exists(existRequest, RequestOptions.DEFAULT)) {
                    GetAliasesRequest getAliasesRequest = new GetAliasesRequest();
                    getAliasesRequest.indices(index);
                    GetAliasesResponse getAliasesResponse = esClient.indices().getAlias(getAliasesRequest, RequestOptions.DEFAULT);
                    List<String> existAlias = new ArrayList<>();
                    //Map<String, Set<AliasMetaData>> map1 = getAliasesResponse.getAliases();
                    //返回请求的别名 ,forEach((k, v)---> v.forEach--》遍历索引下以包含的枚举事件 【v 是 list 集合 】
                    //将 枚举事件 添加到 list集合中   existAlias.add(t.alias())
                    getAliasesResponse.getAliases().forEach((k, v) -> {
                        v.forEach(t -> existAlias.add(t.alias()));  //查看AliasMetaData 类 ，设计IndexMetaData
                    });
                    System.out.println(existAlias);
                    // existAlias  保存当天的 枚举事件  ==》依次推到，后期会一直 add 进去，越来越多
                    // 所以统计时，去掉已有的重复事件--同一周的事件 统计，开始时间都相同=》同一index
                    // existAlias.stream()  List--返回一个顺序的{@code流}，该集合作为其源。
                    // .filter 返回由该流中与给定谓词==匹配==的元素组成的流。  s 是集合中 索引-->对应的枚举事件
                    // entry --->是当前 index, list{}
                    //  list{ } 里面没有包含 s 就保留下 放到 remove 集合 【删除无效时间 ？枚举中不存在的？】 ????????????????????
                    List<String> remove =
                            existAlias.stream().filter(s -> !entry.getValue().contains(s)).collect(Collectors.toList());
                    //当前 list{ } 还没有统计的                                                          ????????????????????
                    List<String> add = entry.getValue().stream().filter(s -> !existAlias.contains(s)).collect(Collectors.toList());

                    //给索引设置别名：new IndicesAliasesRequest();
                    IndicesAliasesRequest update = new IndicesAliasesRequest();
                    // AliasActions  请求对一个或多个索引和别名组合采取一个或多个操作。
                    // addAliasAction(AliasActions aliasAction)
                    if (remove.size() > 0) //删除别名:{}
                        //addAliasAction：向该request 添加操作并验证它。---返回还是 request
                        //AliasActions【内部类】：请求对一个或多个索引和别名组合采取一个或多个操作。
                        update.addAliasAction(IndicesAliasesRequest.AliasActions.remove().index(index).aliases(remove.toArray(new String[]{})));
                    if (add.size() > 0)  // 添加别名{}
                        update.addAliasAction(IndicesAliasesRequest.AliasActions.add().index(index).aliases(add.toArray(new String[]{})));
                    if (update.getAliasActions().size() > 0) {
                        //updateAliases 使用索引别名API更新别名。
                        esClient.indices().updateAliases(update, RequestOptions.DEFAULT);
                        log.info("更新index:{} 删除别名:{} 添加别名{}", index, remove.toString(), add.toString());
                    }


                }

            }
        } catch (IOException e) {
            log.error("设置别名失败");
        }
    }
}
