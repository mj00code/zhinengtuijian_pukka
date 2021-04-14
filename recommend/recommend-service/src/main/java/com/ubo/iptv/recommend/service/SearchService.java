package com.ubo.iptv.recommend.service;

import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.StarESSearchDTO;
import com.ubo.iptv.entity.gdgd.TypeKindESSearchDTO;
import com.ubo.iptv.entity.gdgd.recommend.RecommendEngineDTO;
import com.ubo.iptv.recommend.entity.MediaDoc;
import com.ubo.iptv.recommend.entity.MediaSort;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface SearchService {

    public List<MediaDoc> findMediaList(String category, String catalog, List<MediaSort> sorts, List<String> ignoreList, int size);


    /**
     * date前number天内每天都曝光但是没点击的用户列表
     *
     * @param date
     * @param number
     * @return
     */
    public List<String> exposureNoClick(LocalDate date, int number);


    /**
     * media_id对应媒资的各event_type次数
     *
     * @param media_id
     * @param sys_id
     * @return
     */

    public Map<String, Long> mediaCountByEventType(int media_id, String sys_id, LocalDate date);

    /**
     * 根据TOP推荐类型,推荐题材 查询媒资 默认返回180条
     *
     * @param sceneDTO        排序
     * @param excludeMap      排除列表
     * @param recommendEngine 检索条件(类型,题材)
     * @param isColdBoot      是否冷启动
     * @return
     */
    public List<TypeKindESSearchDTO> findMediaByTypeAndKind(SceneDTO sceneDTO, LinkedHashMap excludeMap, RecommendEngineDTO recommendEngine, Boolean isColdBoot);


    /**
     * 根据TOP推荐类型,推荐题材 查询媒资 默认返回180条
     *
     * @param sceneDTO        排序
     * @param excludeMap      排除列表
     * @param recommendEngine 检索条件(类型,题材)
     * @param isColdBoot      是否冷启动
     * @return
     */
    public List<TypeKindESSearchDTO> findMediaByType(SceneDTO sceneDTO, LinkedHashMap excludeMap, RecommendEngineDTO recommendEngine, Boolean isColdBoot);


    /**
     * 前day天出现次数做多的top size条明星
     *
     * @param day
     * @param size
     * @return
     */
    public List<String> topActors(int day, int size);


    /**
     * 根据明星推荐媒资
     *
     * @param sceneDTO   排序
     * @param excludeMap 排重
     * @param actors     明星
     * @param isColdBoot 是否冷启动
     * @param size       数量
     * @return
     */
    public List<StarESSearchDTO> findMediaByActors(SceneDTO sceneDTO, LinkedHashMap excludeMap, Map<Integer, List<String>> actors, Boolean isColdBoot, Integer size);


}
