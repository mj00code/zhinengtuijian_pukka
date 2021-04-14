package cn.pukkasoft.datasync.model;

import java.util.Date;
import java.util.List;

public class Contentinfo {
    private static final long serialVersionUID = 5454155825314638L;

    /**
     * 内容信息
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 名称(英文)
     */
    private String nameen;

    /**
     * 编号
     */
    private String code;

    /**
     * 父ID
     */
    private Integer parentid;

    /**
     * 演员列表
     */
    private String actordisplay;

    /**
     * 导演列表英文
     */
    private String writerdisplayen;

    /**
     * 演员列表
     */
    private String writerdisplay;

    /**
     * 导演列表英文
     */
    private String actordisplayen;

    /**
     * 描述
     */
    private String description;

    /**
     * 描述(英文)
     */
    private String descriptionen;

    /**
     * 地区(中文)
     */
    private String originalcountry;

    /**
     * 地区(英文)
     */
    private String originalcountryen;

    /**
     * 语言(中文)
     */
    private String language;

    /**
     * 语言(英文)
     */
    private String languageen;

    /**
     * 上映年份
     */
    private String releaseyear;

    /**
     * 英文上映年份
     */
    private String releaseyearen;

    /**
     * 电视剧集数
     */
    private Integer episodecount;

    /**
     * 电视剧第几集
     */
    private Integer episodeindex;

    /**
     * 影片时长(分钟)
     */
    private Integer duration;

    /**
     * 缩略图
     */
    private String thumbimg;

    /**
     * 海报
     */
    private String posterimg;

    /**
     * 剧照
     */
    private String stillimg;

    /**
     * 剧照2
     */
    private String stillimg2;

    /**
     * 评分
     */
    private String score;

    /**
     * 点播次数
     */
    private Integer hitcount;

    /**
     * 推荐次数
     */
    private Integer recommendcount;

    /**
     * 上传时间
     */
    private String createtimeString;

    private Long createtimelong;

    /**
     * 更新时间
     */
    private String updatetimeString;

    private Long updatetimelong;

    /**
     * 内容ID
     */
    private Integer contentid;

    /**
     * 内容编号
     */
    private String contentcode;

    /**
     * 内容类型
     */
    private Integer contenttype;

    /**
     * 产地ID
     */
    private Integer contentcountryid;

    /**
     * 年份ID
     */
    private Integer contentyearid;

    /**
     * 分类ID
     */
    private Integer categoryid;

    /**
     * 分类名称
     */
    private String categoryname;

    /**
     * 分类名称(英文)
     */
    private String categorynameen;

    /**
     * 栏目
     */
    private String catalogid;

    /**
     * 栏目
     */
    private String catalogname;

    /**
     * 是否PPV
     */
    private Integer isppv;

    /**
     * PPV价格
     */
    private String ppvprice;

    /**
     * PPV时长(小时)
     */
    private Integer ppvduration;

    /**
     * 所属CP
     */
    private String cpname;

    /**
     * 类型
     */
    private String contentkindid;

    /**
     * 类型
     */
    private String contentkindname;

    /**
     * 类型(英文)
     */
    private String contentkindnameen;

    /**
     * 发行方
     */
    private String producer;

    private Integer sequence;

    /**
     * CPID
     */
    private Integer cpid;

    /**
     * SPID
     */
    private Integer spid;

    private String spname;

    private Integer seriesid;

    private List<Contentinfo> subcontents;

    /**
     * PC终端有效期
     */
    private Date pc_starttime;

    private Long pc_starttimelong;

    private Date pc_endtime;

    private Long pc_endtimelong;

    /**
     * STB终端有效期
     */
    private Date stb_starttime;

    private Long stb_starttimelong;

    private Date stb_endtime;

    private Long stb_endtimelong;

    /**
     * Mobile终端有效期
     */
    private Date mobile_starttime;

    private Long mobile_starttimelong;

    private Date mobile_endtime;

    private Long mobile_endtimelong;

    /**
     * 缩略图(16:9)
     */
    private String thumbimg2;

    /**
     * 海报(16:9)
     */
    private String posterimg2;

    /**
     * 码率
     */
    private String ft;

    /**
     * m3u8播放playurl
     */
    private String playUrl;

    private String thumbimg3;

    private String posterimg3;

    /**
     * 排序方式 1：正序，2：倒序
     */
    private Integer orderingtype;

    /**
     * DRM厂商
     */
    private String drmVendors;

    /**
     * 是否使用DRM
     */
    private String isDrm;

    /**
     * 是否使用第三方播放链接
     */
    private String isThirdPart;

    /**
     * 推荐海报
     */
    private String extendimg;

    /**
     * 中间图
     */
    private String midimg2;

    private String midimg;

    private String midimg3;

    /**
     * 更新至多少集
     */
    private Integer updatedepisodes;

    /**
     * 是否免费  0.false  1.true
     */
    private Integer isFree;

    /**
     * 是否包月  0.false  1.true
     */
    private Integer isMonthly;

    /**
     * 包月价格
     */
    private String monthlyPrice;

    /**
     * 图标
     */
    private String icon;

    /**
     * 频道logo
     */
    private String channelLogo;

    private String cornername;
    private String cornerpicurl;

    private Integer isschedule;

    private Integer isbackchannel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameen() {
        return nameen;
    }

    public void setNameen(String nameen) {
        this.nameen = nameen;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }

    public String getActordisplay() {
        return actordisplay;
    }

    public void setActordisplay(String actordisplay) {
        this.actordisplay = actordisplay;
    }

    public String getWriterdisplayen() {
        return writerdisplayen;
    }

    public void setWriterdisplayen(String writerdisplayen) {
        this.writerdisplayen = writerdisplayen;
    }

    public String getWriterdisplay() {
        return writerdisplay;
    }

    public void setWriterdisplay(String writerdisplay) {
        this.writerdisplay = writerdisplay;
    }

    public String getActordisplayen() {
        return actordisplayen;
    }

    public void setActordisplayen(String actordisplayen) {
        this.actordisplayen = actordisplayen;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionen() {
        return descriptionen;
    }

    public void setDescriptionen(String descriptionen) {
        this.descriptionen = descriptionen;
    }

    public String getOriginalcountry() {
        return originalcountry;
    }

    public void setOriginalcountry(String originalcountry) {
        this.originalcountry = originalcountry;
    }

    public String getOriginalcountryen() {
        return originalcountryen;
    }

    public void setOriginalcountryen(String originalcountryen) {
        this.originalcountryen = originalcountryen;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguageen() {
        return languageen;
    }

    public void setLanguageen(String languageen) {
        this.languageen = languageen;
    }

    public String getReleaseyear() {
        return releaseyear;
    }

    public void setReleaseyear(String releaseyear) {
        this.releaseyear = releaseyear;
    }

    public String getReleaseyearen() {
        return releaseyearen;
    }

    public void setReleaseyearen(String releaseyearen) {
        this.releaseyearen = releaseyearen;
    }

    public Integer getEpisodecount() {
        return episodecount;
    }

    public void setEpisodecount(Integer episodecount) {
        this.episodecount = episodecount;
    }

    public Integer getEpisodeindex() {
        return episodeindex;
    }

    public void setEpisodeindex(Integer episodeindex) {
        this.episodeindex = episodeindex;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getThumbimg() {
        return thumbimg;
    }

    public void setThumbimg(String thumbimg) {
        this.thumbimg = thumbimg;
    }

    public String getPosterimg() {
        return posterimg;
    }

    public void setPosterimg(String posterimg) {
        this.posterimg = posterimg;
    }

    public String getStillimg() {
        return stillimg;
    }

    public void setStillimg(String stillimg) {
        this.stillimg = stillimg;
    }

    public String getStillimg2() {
        return stillimg2;
    }

    public void setStillimg2(String stillimg2) {
        this.stillimg2 = stillimg2;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Integer getHitcount() {
        return hitcount;
    }

    public void setHitcount(Integer hitcount) {
        this.hitcount = hitcount;
    }

    public Integer getRecommendcount() {
        return recommendcount;
    }

    public void setRecommendcount(Integer recommendcount) {
        this.recommendcount = recommendcount;
    }

    public String getCreatetimeString() {
        return createtimeString;
    }

    public void setCreatetimeString(String createtimeString) {
        this.createtimeString = createtimeString;
    }

    public Long getCreatetimelong() {
        return createtimelong;
    }

    public void setCreatetimelong(Long createtimelong) {
        this.createtimelong = createtimelong;
    }

    public String getUpdatetimeString() {
        return updatetimeString;
    }

    public void setUpdatetimeString(String updatetimeString) {
        this.updatetimeString = updatetimeString;
    }

    public Long getUpdatetimelong() {
        return updatetimelong;
    }

    public void setUpdatetimelong(Long updatetimelong) {
        this.updatetimelong = updatetimelong;
    }

    public Integer getContentid() {
        return contentid;
    }

    public void setContentid(Integer contentid) {
        this.contentid = contentid;
    }

    public String getContentcode() {
        return contentcode;
    }

    public void setContentcode(String contentcode) {
        this.contentcode = contentcode;
    }

    public Integer getContenttype() {
        return contenttype;
    }

    public void setContenttype(Integer contenttype) {
        this.contenttype = contenttype;
    }

    public Integer getContentcountryid() {
        return contentcountryid;
    }

    public void setContentcountryid(Integer contentcountryid) {
        this.contentcountryid = contentcountryid;
    }

    public Integer getContentyearid() {
        return contentyearid;
    }

    public void setContentyearid(Integer contentyearid) {
        this.contentyearid = contentyearid;
    }

    public Integer getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(Integer categoryid) {
        this.categoryid = categoryid;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getCategorynameen() {
        return categorynameen;
    }

    public void setCategorynameen(String categorynameen) {
        this.categorynameen = categorynameen;
    }

    public String getCatalogid() {
        return catalogid;
    }

    public void setCatalogid(String catalogid) {
        this.catalogid = catalogid;
    }

    public String getCatalogname() {
        return catalogname;
    }

    public void setCatalogname(String catalogname) {
        this.catalogname = catalogname;
    }

    public Integer getIsppv() {
        return isppv;
    }

    public void setIsppv(Integer isppv) {
        this.isppv = isppv;
    }

    public String getPpvprice() {
        return ppvprice;
    }

    public void setPpvprice(String ppvprice) {
        this.ppvprice = ppvprice;
    }

    public Integer getPpvduration() {
        return ppvduration;
    }

    public void setPpvduration(Integer ppvduration) {
        this.ppvduration = ppvduration;
    }

    public String getCpname() {
        return cpname;
    }

    public void setCpname(String cpname) {
        this.cpname = cpname;
    }

    public String getContentkindid() {
        return contentkindid;
    }

    public void setContentkindid(String contentkindid) {
        this.contentkindid = contentkindid;
    }

    public String getContentkindname() {
        return contentkindname;
    }

    public void setContentkindname(String contentkindname) {
        this.contentkindname = contentkindname;
    }

    public String getContentkindnameen() {
        return contentkindnameen;
    }

    public void setContentkindnameen(String contentkindnameen) {
        this.contentkindnameen = contentkindnameen;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getCpid() {
        return cpid;
    }

    public void setCpid(Integer cpid) {
        this.cpid = cpid;
    }

    public Integer getSpid() {
        return spid;
    }

    public void setSpid(Integer spid) {
        this.spid = spid;
    }

    public String getSpname() {
        return spname;
    }

    public void setSpname(String spname) {
        this.spname = spname;
    }

    public Integer getSeriesid() {
        return seriesid;
    }

    public void setSeriesid(Integer seriesid) {
        this.seriesid = seriesid;
    }

    public List<Contentinfo> getSubcontents() {
        return subcontents;
    }

    public void setSubcontents(List<Contentinfo> subcontents) {
        this.subcontents = subcontents;
    }

    public Date getPc_starttime() {
        return pc_starttime;
    }

    public void setPc_starttime(Date pc_starttime) {
        this.pc_starttime = pc_starttime;
    }

    public Long getPc_starttimelong() {
        return pc_starttimelong;
    }

    public void setPc_starttimelong(Long pc_starttimelong) {
        this.pc_starttimelong = pc_starttimelong;
    }

    public Date getPc_endtime() {
        return pc_endtime;
    }

    public void setPc_endtime(Date pc_endtime) {
        this.pc_endtime = pc_endtime;
    }

    public Long getPc_endtimelong() {
        return pc_endtimelong;
    }

    public void setPc_endtimelong(Long pc_endtimelong) {
        this.pc_endtimelong = pc_endtimelong;
    }

    public Date getStb_starttime() {
        return stb_starttime;
    }

    public void setStb_starttime(Date stb_starttime) {
        this.stb_starttime = stb_starttime;
    }

    public Long getStb_starttimelong() {
        return stb_starttimelong;
    }

    public void setStb_starttimelong(Long stb_starttimelong) {
        this.stb_starttimelong = stb_starttimelong;
    }

    public Date getStb_endtime() {
        return stb_endtime;
    }

    public void setStb_endtime(Date stb_endtime) {
        this.stb_endtime = stb_endtime;
    }

    public Long getStb_endtimelong() {
        return stb_endtimelong;
    }

    public void setStb_endtimelong(Long stb_endtimelong) {
        this.stb_endtimelong = stb_endtimelong;
    }

    public Date getMobile_starttime() {
        return mobile_starttime;
    }

    public void setMobile_starttime(Date mobile_starttime) {
        this.mobile_starttime = mobile_starttime;
    }

    public Long getMobile_starttimelong() {
        return mobile_starttimelong;
    }

    public void setMobile_starttimelong(Long mobile_starttimelong) {
        this.mobile_starttimelong = mobile_starttimelong;
    }

    public Date getMobile_endtime() {
        return mobile_endtime;
    }

    public void setMobile_endtime(Date mobile_endtime) {
        this.mobile_endtime = mobile_endtime;
    }

    public Long getMobile_endtimelong() {
        return mobile_endtimelong;
    }

    public void setMobile_endtimelong(Long mobile_endtimelong) {
        this.mobile_endtimelong = mobile_endtimelong;
    }

    public String getThumbimg2() {
        return thumbimg2;
    }

    public void setThumbimg2(String thumbimg2) {
        this.thumbimg2 = thumbimg2;
    }

    public String getPosterimg2() {
        return posterimg2;
    }

    public void setPosterimg2(String posterimg2) {
        this.posterimg2 = posterimg2;
    }

    public String getFt() {
        return ft;
    }

    public void setFt(String ft) {
        this.ft = ft;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getThumbimg3() {
        return thumbimg3;
    }

    public void setThumbimg3(String thumbimg3) {
        this.thumbimg3 = thumbimg3;
    }

    public String getPosterimg3() {
        return posterimg3;
    }

    public void setPosterimg3(String posterimg3) {
        this.posterimg3 = posterimg3;
    }

    public Integer getOrderingtype() {
        return orderingtype;
    }

    public void setOrderingtype(Integer orderingtype) {
        this.orderingtype = orderingtype;
    }

    public String getDrmVendors() {
        return drmVendors;
    }

    public void setDrmVendors(String drmVendors) {
        this.drmVendors = drmVendors;
    }

    public String getIsDrm() {
        return isDrm;
    }

    public void setIsDrm(String isDrm) {
        this.isDrm = isDrm;
    }

    public String getIsThirdPart() {
        return isThirdPart;
    }

    public void setIsThirdPart(String isThirdPart) {
        this.isThirdPart = isThirdPart;
    }

    public String getExtendimg() {
        return extendimg;
    }

    public void setExtendimg(String extendimg) {
        this.extendimg = extendimg;
    }

    public String getMidimg2() {
        return midimg2;
    }

    public void setMidimg2(String midimg2) {
        this.midimg2 = midimg2;
    }

    public String getMidimg() {
        return midimg;
    }

    public void setMidimg(String midimg) {
        this.midimg = midimg;
    }

    public String getMidimg3() {
        return midimg3;
    }

    public void setMidimg3(String midimg3) {
        this.midimg3 = midimg3;
    }

    public Integer getUpdatedepisodes() {
        return updatedepisodes;
    }

    public void setUpdatedepisodes(Integer updatedepisodes) {
        this.updatedepisodes = updatedepisodes;
    }

    public Integer getIsFree() {
        return isFree;
    }

    public void setIsFree(Integer isFree) {
        this.isFree = isFree;
    }

    public Integer getIsMonthly() {
        return isMonthly;
    }

    public void setIsMonthly(Integer isMonthly) {
        this.isMonthly = isMonthly;
    }

    public String getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(String monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getChannelLogo() {
        return channelLogo;
    }

    public void setChannelLogo(String channelLogo) {
        this.channelLogo = channelLogo;
    }

    public String getCornername() {
        return cornername;
    }

    public void setCornername(String cornername) {
        this.cornername = cornername;
    }

    public String getCornerpicurl() {
        return cornerpicurl;
    }

    public void setCornerpicurl(String cornerpicurl) {
        this.cornerpicurl = cornerpicurl;
    }

    public Integer getIsschedule() {
        return isschedule;
    }

    public void setIsschedule(Integer isschedule) {
        this.isschedule = isschedule;
    }

    public Integer getIsbackchannel() {
        return isbackchannel;
    }

    public void setIsbackchannel(Integer isbackchannel) {
        this.isbackchannel = isbackchannel;
    }
}

