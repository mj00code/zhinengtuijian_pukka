package cn.pukkasoft.datasync.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 媒资图片关系对象 t_content_pic_url
 *
 * @author userpoints
 * @date 2020-11-22
 */
public class TContentPicUrl {
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 媒资类型
     */
    private Integer contentType;

    /**
     * 内容code
     */
    private String contentCode;

    /**
     * 媒资名称
     */
    private String mediaName;

    /**
     * 图片地址
     */
    private String picUrl;

    private Date createTime;
    private Date updateTime;
    private String cornerPicurl;
    private Integer cpid;
    private Integer spid;
    private String cpName;
    private String spName;

    public String getCornerPicurl() {
        return cornerPicurl;
    }

    public void setCornerPicurl(String cornerPicurl) {
        this.cornerPicurl = cornerPicurl;
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

    public String getCpName() {
        return cpName;
    }

    public void setCpName(String cpName) {
        this.cpName = cpName;
    }

    public String getSpName() {
        return spName;
    }

    public void setSpName(String spName) {
        this.spName = spName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 运营商标识1移动2联通
     */

    private Integer platform;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentCode(String contentCode) {
        this.contentCode = contentCode;
    }

    public String getContentCode() {
        return contentCode;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public Integer getPlatform() {
        return platform;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("contentType", getContentType())
                .append("contentCode", getContentCode())
                .append("mediaName", getMediaName())
                .append("picUrl", getPicUrl())
                .append("platform", getPlatform())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
