package com.ubo.iptv.manage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.constant.RedisConstant;
import com.ubo.iptv.core.enums.MediaTypeEnum;
import com.ubo.iptv.core.enums.SysEnum;
import com.ubo.iptv.core.util.DateUtil;
import com.ubo.iptv.entity.gdgd.MediaDTO;
import com.ubo.iptv.manage.requset.BlacklistAddDTO;
import com.ubo.iptv.manage.response.BlacklistVO;
import com.ubo.iptv.manage.service.BlacklistService;
import com.ubo.iptv.manage.service.RedisService;
import com.ubo.iptv.mybatis.gsgd.entity.GSContentinfoDO;
import com.ubo.iptv.mybatis.gsgd.mapper.GSContentinfoMapper;
import com.ubo.iptv.mybatis.recommend.entity.MediaBlacklistDO;
import com.ubo.iptv.mybatis.recommend.mapper.MediaBlacklistMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xuning
 * @Date: 2020-11-09
 */
@Service
@Slf4j
public class BlacklistServiceImpl implements BlacklistService {

    @Autowired
    private RedisService redisService;
    @Resource
    private MediaBlacklistMapper mediaBlacklistMapper;
    @Resource
    private GSContentinfoMapper gsContentinfoMapper;

    @Override
    public CommonResponse cacheBlacklist() {
        LambdaQueryWrapper<MediaBlacklistDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MediaBlacklistDO::getStatus, 1);
        List<MediaBlacklistDO> list = mediaBlacklistMapper.selectList(wrapper);

        List<MediaBlacklistDO> mList = new ArrayList<>();
        List<MediaBlacklistDO> uList = new ArrayList<>();
        List<MediaBlacklistDO> tList = new ArrayList<>();
        list.forEach(s -> {
            // 移动
            if ("m".equals(s.getSysId())) {
                mList.add(s);
            }
            // 联通
            else if ("u".equals(s.getSysId())) {
                uList.add(s);
            }
            // 电信
            else if ("t".equals(s.getSysId())) {
                tList.add(s);
            }
        });
        // 同步至redis
        boolean redisResponse = redisService.set(String.format(RedisConstant.MEDIA_BLACKLIST_KEY, "m"), JSONArray.toJSONString(mList));
        if (!redisResponse) {
            log.error("Redis set error: m_media_blacklist");
            return CommonResponse.FAIL;
        }
        redisResponse = redisService.set(String.format(RedisConstant.MEDIA_BLACKLIST_KEY, "u"), JSONArray.toJSONString(uList));
        if (!redisResponse) {
            log.error("Redis set error: u_media_blacklist");
            return CommonResponse.FAIL;
        }
        redisResponse = redisService.set(String.format(RedisConstant.MEDIA_BLACKLIST_KEY, "t"), JSONArray.toJSONString(tList));
        if (!redisResponse) {
            log.error("Redis set error: t_media_blacklist");
            return CommonResponse.FAIL;
        }
        return CommonResponse.SUCCESS;
    }

    @Override
    public CommonResponse<IPage<BlacklistVO>> listBlacklist(String content, Page page) {
        LambdaQueryWrapper<MediaBlacklistDO> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(content)) {
            wrapper.like(MediaBlacklistDO::getMediaName, content).or().like(MediaBlacklistDO::getMediaId, content);
        }
        wrapper.orderByDesc(MediaBlacklistDO::getId);
        IPage<MediaBlacklistDO> mediaBlacklistDOIPage = mediaBlacklistMapper.selectPage(page, wrapper);

        List<BlacklistVO> voList = new ArrayList<>();
        mediaBlacklistDOIPage.getRecords().forEach(s -> {
            BlacklistVO vo = new BlacklistVO();
            vo.setId(s.getId());
            vo.setIspId(s.getSysId());
            vo.setIspName(SysEnum.description(s.getSysId()));
            vo.setMediaId(s.getMediaId());
            vo.setMediaType(s.getMediaType());
            vo.setMediaName(s.getMediaName());
            vo.setMediaCode(s.getMediaCode());
            vo.setMediaTypeName(MediaTypeEnum.description(s.getMediaType()));
            vo.setStatus(s.getStatus() ? 1 : 0);
            vo.setStartTime(DateUtil.format(s.getStartTime()));
            vo.setEndTime(DateUtil.format(s.getExpireTime()));
            voList.add(vo);
        });
        return CommonResponse.success(page.setRecords(voList));
    }

    @Override
    public CommonResponse<List<BlacklistVO>> searchMedia(String sysId, String content) {
        List<GSContentinfoDO> contentinfoDOList = gsContentinfoMapper.searchMedia(null, content);
        List<BlacklistVO> voList = new ArrayList<>();
        contentinfoDOList.forEach(s -> {
            //海报url为空,则不能添加
            String mediaInfoString = redisService.get(String.format(RedisConstant.MEDIA_KEY, s.getId()));
            if (StringUtils.isNotEmpty(mediaInfoString)) {
                // 转换成统一media对象
                MediaDTO mediaDTO = JSONObject.parseObject(mediaInfoString, MediaDTO.class);
                //如果运营商不同,则跳过
                if (StringUtils.isNotEmpty(sysId) && !mediaDTO.getSysId().equals(sysId)) {
                    return;
                }
                // 是否已经在黑名单
                Integer count = mediaBlacklistMapper.selectCount(new LambdaQueryWrapper<MediaBlacklistDO>().eq(MediaBlacklistDO::getMediaId, mediaDTO.getMediaId()));
                BlacklistVO vo = new BlacklistVO();
                vo.setIspId(mediaDTO.getSysId());
                vo.setIspName(SysEnum.description(mediaDTO.getSysId()));
                vo.setMediaId(mediaDTO.getMediaId());
                vo.setMediaCode(mediaDTO.getMediaCode());
                vo.setMediaName(mediaDTO.getName());
                vo.setMediaType(mediaDTO.getMediaType());
                vo.setMediaTypeName(mediaDTO.getMediaTypeName());
                vo.setStatus(count);
                voList.add(vo);
            }
        });
        return CommonResponse.success(voList);
    }

    @Override
    public CommonResponse addBlackList(BlacklistAddDTO dto) {
        // 是否已经在黑名单
        Integer count = mediaBlacklistMapper.selectCount(new LambdaQueryWrapper<MediaBlacklistDO>().eq(MediaBlacklistDO::getMediaId, dto.getMediaId()));
        if (count > 0) {
            log.error("media already in blacklist: {}", dto);
            return CommonResponse.fail(1, "媒资已经在黑名单中了");
        }
        // 加入黑名单
        MediaBlacklistDO entity = new MediaBlacklistDO();
        entity.setSysId(dto.getIspId());
        entity.setMediaType(dto.getMediaType());
        entity.setMediaId(dto.getMediaId());
        entity.setMediaCode(dto.getMediaCode());
        entity.setMediaName(dto.getMediaName());
        entity.setStatus(true);
        if (StringUtils.isNotBlank(dto.getStartTime())) {
            entity.setStartTime(DateUtil.parse(dto.getStartTime() + " 00:00:00"));
        }
        if (StringUtils.isNotBlank(dto.getEndTime())) {
            entity.setExpireTime(DateUtil.parse(dto.getEndTime() + " 23:59:59"));
        }
        mediaBlacklistMapper.insert(entity);
        return CommonResponse.SUCCESS;
    }

    @Override
    public CommonResponse removeBlackList(Long id) {
        int res = mediaBlacklistMapper.deleteById(id);
        if (res == 0) {
            return CommonResponse.FAIL;
        }
        return CommonResponse.SUCCESS;
    }
}
