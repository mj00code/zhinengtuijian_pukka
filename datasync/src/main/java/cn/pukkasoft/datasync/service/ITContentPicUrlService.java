package cn.pukkasoft.datasync.service;

import cn.pukkasoft.datasync.advice.ApiResponse;
import cn.pukkasoft.datasync.model.TContentPicUrl;

import java.util.List;

/**
 * 媒资图片关系Service接口
 *
 * @author userpoints
 * @date 2020-11-22
 */
public interface ITContentPicUrlService {
    /**
     * 查询媒资图片关系
     *
     * @param id 媒资图片关系ID
     * @return 媒资图片关系
     */
    public TContentPicUrl selectTContentPicUrlById(Long id);

    /**
     * 查询媒资图片关系列表
     *
     * @param tContentPicUrl 媒资图片关系
     * @return 媒资图片关系集合
     */
    public List<TContentPicUrl> selectTContentPicUrlList(TContentPicUrl tContentPicUrl);

    /**
     * 新增媒资图片关系
     *
     * @param tContentPicUrl 媒资图片关系
     * @return 结果
     */
    public int insertTContentPicUrl(TContentPicUrl tContentPicUrl);

    /**
     * 修改媒资图片关系
     *
     * @param tContentPicUrl 媒资图片关系
     * @return 结果
     */
    public int updateTContentPicUrl(TContentPicUrl tContentPicUrl);

    /**
     * 批量删除媒资图片关系
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
//    public int deleteTContentPicUrlByIds(String ids);

    /**
     * 删除媒资图片关系信息
     *
     * @param id 媒资图片关系ID
     * @return 结果
     */
    public int deleteTContentPicUrlById(Long id);

    ApiResponse notifyContentStatus(Integer contentType, String contentCode, Integer contentId);

    int getContentPicCount(String contentCode);
}
