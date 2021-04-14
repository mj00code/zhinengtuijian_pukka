package cn.pukkasoft.datasync.model;

/**
 * @Author SHAWN LIAO
 * @ClassName info
 * @Date 2020/12/8 20:03
 * @Description 1
 */
public class ContentInfoQuery {
    private String format;
    private String portalcode;
    private String sptoken;
    private Integer pageindex;
    private Integer pagesize;
    private Integer tmtype;
    private String contentcode;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPortalcode() {
        return portalcode;
    }

    public void setPortalcode(String portalcode) {
        this.portalcode = portalcode;
    }

    public String getSptoken() {
        return sptoken;
    }

    public void setSptoken(String sptoken) {
        this.sptoken = sptoken;
    }

    public Integer getPageindex() {
        return pageindex;
    }

    public void setPageindex(Integer pageindex) {
        this.pageindex = pageindex;
    }

    public Integer getPagesize() {
        return pagesize;
    }

    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }

    public Integer getTmtype() {
        return tmtype;
    }

    public void setTmtype(Integer tmtype) {
        this.tmtype = tmtype;
    }

    public String getContentcode() {
        return contentcode;
    }

    public void setContentcode(String contentcode) {
        this.contentcode = contentcode;
    }
}
