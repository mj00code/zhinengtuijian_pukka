package com.ubo.iptv.manage.service;

import com.ubo.iptv.manage.entity.OrderedUser;
import com.ubo.iptv.manage.requset.TagConditionDTO;
import com.ubo.iptv.manage.response.AbstractTagSummeryVO;
import com.ubo.iptv.manage.response.DailyDashBoardVO;
import com.ubo.iptv.manage.response.TagMediaSummaryVO;
import com.ubo.iptv.manage.response.TagUserSummaryVO;
import com.ubo.iptv.mybatis.recommend.entity.TagDO;
import com.ubo.iptv.mybatis.recommend.entity.TagGroupsDO;
import io.swagger.models.auth.In;
import javafx.util.Pair;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.collect.Tuple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EsReportService {

    /**
     * 当天的各小时的活跃用户数
     *
     * @param index
     * @param startTime
     * @param endTime
     * @return
     */
    public Tuple<Number, List<Number>> findOnlineUserPartionByHour(String index, LocalDateTime startTime, LocalDateTime endTime);


    /**
     * 当天各小时的播放用户
     *
     * @param index
     * @param eventType
     * @param startTime
     * @param endTime
     * @return
     */
    public Tuple<Number, List<Number>> findPlayUserPartionByHour(String index, String eventType, LocalDateTime startTime, LocalDateTime endTime);


    /**
     * 当天各小时的订单数据
     *
     * @param index
     * @param startTime
     * @param endTime
     * @return
     */
    public SearchResponse findOrderDataPartionByHour(String index, LocalDateTime startTime, LocalDateTime endTime);


    /**
     * 区间内每天的订购数据
     *
     * @param start
     * @param end
     * @return
     */
    public SearchResponse intervalOrderData(LocalDate start, LocalDate end);


    /**
     * 区间内有订购的用户
     *
     * @param start
     * @param end
     * @return
     */
    public List<OrderedUser> intervalOrderedUserIds(LocalDate start, LocalDate end);

    /**
     * 时间段内的订购类型
     *
     * @param start
     * @param end
     * @return
     */
    public SearchResponse dailyProductType(LocalDate start, LocalDate end);


    /**
     * 进入详情页次数 详情页sceneId 14 15 16
     *
     * @param start
     * @param end
     * @return
     */
    public int viewCount(LocalDate start, LocalDate end);


    /**
     * 开机次数 详情页sceneId 14 15 16
     *
     * @param start
     * @param end
     * @return
     */
    public int openCount(LocalDate start, LocalDate end);

    /**
     * 区间内每日平均点播时长
     *
     * @param start
     * @param end
     * @return
     */
    public DailyDashBoardVO vodAvgTimes(LocalDate start, LocalDate end);

    /**
     * 区间内每日点播人数
     *
     * @param start
     * @param end
     * @return
     */
    public DailyDashBoardVO vodUserCounts(LocalDate start, LocalDate end);

    /**
     * 区间每日有效点播
     *
     * @param start
     * @param end
     * @return
     */
    public DailyDashBoardVO vodEffective(LocalDate start, LocalDate end);

    /**
     * 区间没媒资点播时长占比
     *
     * @param start
     * @param end
     * @return
     */
    public DailyDashBoardVO vodMediaTypes(LocalDate start, LocalDate end);

    /**
     * 区间内查看直播的人数
     *
     * @param start
     * @param end
     * @return
     */
    public DailyDashBoardVO tvOnline(LocalDate start, LocalDate end);


    /**
     * 区间内人数最多的频道每日访问人数
     *
     * @param start
     * @param end
     * @return
     */
    public Map<String, List<String>> tvUserTops(LocalDate start, LocalDate end);


    /**
     * 区间内频道的访问时间
     *
     * @param start
     * @param end
     * @return
     */
    public Map<String, List<String>> tvTimeTops(LocalDate start, LocalDate end);

    /**
     * 区间内推荐位每天的点击次数
     *
     * @param start
     * @param end
     * @return
     */
    public SearchResponse recommendClick(LocalDate start, LocalDate end);


//    Pair<String, List<TagUserSummaryVO>> searchUserGroup(TagConditionDTO dto);
//
//    List<TagUserSummaryVO> searchUserGroup(String queryCondition);
//
//    Pair<String, List<TagMediaSummaryVO>> searchMediaGroup(TagConditionDTO dto);

    List<? extends AbstractTagSummeryVO> groupMatchList(TagGroupsDO tagGroupsDO, Integer size);


    SearchResponse downLoadGroupInfo(Long groupId);

    SearchResponse downLoadGroupInfo(String scrollId);

    SearchResponse groupMatchListWithSummary(TagGroupsDO tagGroupsDO, Set<Long> tags);
}
