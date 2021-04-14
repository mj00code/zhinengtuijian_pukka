package com.ubo.iptv.recommend.util;

import com.ubo.iptv.entity.gdgd.MediaDTO;
import com.ubo.iptv.entity.gdgd.SceneDTO;
import com.ubo.iptv.entity.gdgd.recommend.*;
import com.ubo.iptv.mybatis.recommend.entity.StrategyMediaTypeDO;
import com.ubo.iptv.recommend.enums.MediaChargeFreeEnum;
import com.ubo.iptv.recommend.enums.MediaShowEnum;
import com.ubo.iptv.recommend.response.IPTVMediaVO;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CalculateCoreUtil {

    /**
     * 计算媒资类型,媒资题材推荐的数量
     *
     * @param list
     * @param recommendCommonSize
     */
    public static void calculateSize(List<? extends RecommendCommonDTO> list, Integer recommendCommonSize) {
        if (null == list || list.size() == 0 || recommendCommonSize <= 0) {
            return;
        }
        //中间变量
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            RecommendCommonDTO recommendComon = list.get(i);
            if (i == list.size() - 1) {
                recommendComon.setSize(recommendCommonSize - count);
            } else {
                recommendComon.setSize(recommendComon.getWeight().multiply(new BigDecimal(recommendCommonSize)).setScale(0, BigDecimal.ROUND_UP).intValue());
                count = count + recommendComon.getSize();
            }
        }
        if (list.size() >= 2) {
            while (list.get(list.size() - 1).getSize() < 1) {
                for (int i = list.size() - 1; i > 0; i--) {
                    if (list.get(i).getSize() < list.get(i - 1).getSize()) {
                        list.get(i).setSize(list.get(i).getSize() + 1);
                        list.get(i - 1).setSize(list.get(i - 1).getSize() - 1);
                    }
                }
            }
        }
    }

    /**
     * 计算媒资收费免费的数量(明星推荐跟协同过滤推荐)
     *
     * @param sceneDTO        获取收费比例
     * @param recommendEngine 获取数量
     * @return 收费数量
     */
    public static Integer calculateChargeFreeSize(SceneDTO sceneDTO, RecommendEngineDTO recommendEngine) {
        BigDecimal chargeRatio = BigDecimal.ZERO;
        List<StrategyMediaTypeDO> typeDOList = sceneDTO.getRecommendStrategyDetail().getStrategyMediaTypeList();
        if (!CollectionUtils.isEmpty(typeDOList)) {
            chargeRatio = typeDOList.get(0).getChargeRatio();
        }
        chargeRatio = null == chargeRatio ? BigDecimal.ZERO : chargeRatio;
        Integer recommendComonSize = null == recommendEngine.getSize() ? 0 : recommendEngine.getSize();
        Integer freeSize = (new BigDecimal(1).subtract(chargeRatio)).multiply(new BigDecimal(recommendComonSize)).setScale(0, BigDecimal.ROUND_UP).intValue();
        Integer chargeSize = recommendComonSize - freeSize;
        return chargeSize;
    }

    /**
     * 计算媒资收费免费的数量(题材偏好推荐跟实时题材偏好推荐用)
     *
     * @param mediaTypeTop
     * @param mediaKindTop
     * @param recommendComonSize
     * @return
     */
    public static List<MediaChargeFreeDTO> calculateChargeFreeSize(MediaTypeTopDTO mediaTypeTop, MediaKindTopDTO mediaKindTop, Integer recommendComonSize) {
        List<MediaChargeFreeDTO> mediaChargeFreeList = new ArrayList<>();
        MediaChargeFreeDTO charge = new MediaChargeFreeDTO();
        MediaChargeFreeDTO free = new MediaChargeFreeDTO();
        if (recommendComonSize <= 0) {
            return mediaChargeFreeList;
        }
        //1:mediaKind收付费 2:mediaType收付费
        BigDecimal chargeRatio = new BigDecimal(0);
        if (null != mediaKindTop) {
            //冷启动,直播以外
            charge.setMediaKind(mediaKindTop.getMediaKind());
            free.setMediaKind(mediaKindTop.getMediaKind());
        }
        chargeRatio = mediaTypeTop.getChargeRatio();
        Integer chargeSize = 0;
        Integer freeSize = 0;
        if (chargeRatio.doubleValue() >= 0.5) {
            freeSize = (new BigDecimal(1).subtract(chargeRatio)).multiply(new BigDecimal(recommendComonSize)).setScale(0, BigDecimal.ROUND_UP).intValue();
            chargeSize = recommendComonSize - freeSize;
            if (chargeSize < freeSize) {
                chargeSize = chargeSize + 1;
                freeSize = freeSize - 1;
            }
        } else {
            chargeSize = chargeRatio.multiply(new BigDecimal(recommendComonSize)).setScale(0, BigDecimal.ROUND_UP).intValue();
            freeSize = recommendComonSize - chargeSize;
            if (freeSize < chargeSize) {
                freeSize = freeSize + 1;
                chargeSize = chargeSize - 1;
            }
        }
        charge.setMediaType(mediaTypeTop.getMediaType());
        charge.setIsCharge(MediaChargeFreeEnum.CHARGE.intValue());
        charge.setSize(chargeSize);

        free.setMediaType(mediaTypeTop.getMediaType());
        free.setIsCharge(MediaChargeFreeEnum.FREE.intValue());
        free.setSize(freeSize);
        mediaChargeFreeList.add(charge);
        mediaChargeFreeList.add(free);
        return mediaChargeFreeList;

    }

    public static void setCodeBootTypeKindSizeList(RecommendEngineDTO recommendEngine, SceneDTO strategyConfig) {
        //题材偏好 推荐引擎的数量
        //从数据库中获取每个类型的权重
        List<StrategyMediaTypeDO> strategyMediaTypeList = strategyConfig.getCodeBootStrategyDetail().getStrategyMediaTypeList();
        List<MediaChargeFreeDTO> mediaChargeFreeList = new ArrayList<>();
        List<MediaTypeTopDTO> mediaTypeTopList = new ArrayList<>();
        if (CollectionUtils.isEmpty(strategyMediaTypeList)) {
            //类型中设置所有数量的列表
            recommendEngine.setMediaTypeList(mediaTypeTopList);
            //类型中设置所有数量的列表
            recommendEngine.setMediaChargeFreeList(mediaChargeFreeList);
            return;
        }
        strategyMediaTypeList.forEach(strategyMediaType -> {
            MediaTypeTopDTO mediaTypeTop = new MediaTypeTopDTO();
            BeanUtils.copyProperties(strategyMediaType, mediaTypeTop);
            mediaTypeTopList.add(mediaTypeTop);
        });
        //计算每个类型的数量
        CalculateCoreUtil.calculateSize(mediaTypeTopList, recommendEngine.getSize());

        for (MediaTypeTopDTO mediaTypeTop : mediaTypeTopList) {
            //计算每个题材收费免费数量
            mediaChargeFreeList.addAll(CalculateCoreUtil.calculateChargeFreeSize(mediaTypeTop, null, mediaTypeTop.getSize()));
        }
        //类型中设置所有数量的列表
        recommendEngine.setMediaTypeList(mediaTypeTopList);
        //类型中设置所有数量的列表
        recommendEngine.setMediaChargeFreeList(mediaChargeFreeList);
    }

    /**
     * 编辑返回值
     *
     * @param mediaId
     * @return
     */
    public static IPTVMediaVO getIPTVMediaVO(Integer mediaId, MediaDTO media, Integer mediaShowType, String sysId, Float score) {
        IPTVMediaVO iptvProgramVO = new IPTVMediaVO();
        iptvProgramVO.setMediaId(mediaId);
        iptvProgramVO.setShowType(mediaShowType);
        iptvProgramVO.setMediaType(media.getMediaType().toString());
        iptvProgramVO.setMediaTypeName(media.getMediaTypeName());
        iptvProgramVO.setMediaName(media.getName());
        iptvProgramVO.setMediaCode(media.getMediaCode());
        iptvProgramVO.setSysId(sysId);
        iptvProgramVO.setScore(score);
        iptvProgramVO.setShowTypeName(MediaShowEnum.description(iptvProgramVO.getShowType()));
        iptvProgramVO.setPosterUrl(media.getPosterUrl());
        iptvProgramVO.setContentType(media.getMediaTypeId());
        iptvProgramVO.setIsFree(media.getFree());
        iptvProgramVO.setCornerUrl(media.getCornerUrl());
        iptvProgramVO.setCpId(media.getCpId());
        iptvProgramVO.setCpName(media.getCpName());
        iptvProgramVO.setSpId(media.getSpId());
        iptvProgramVO.setSpName(media.getSpName());
        iptvProgramVO.setDescription( media.getDescription());
        return iptvProgramVO;
    }

}