package cn.pukkasoft.datasync.vo;

/**
 * @Author SHAWN LIAO
 * @ClassName contentInfoUrlVo
 * @Date 2020/11/22 17:42
 * @Description 1
 */
public class ContentInfoUrlVo {
    private String portalcode;
    private String sptoken;
    private String pageindex;
    private String pagesize;
    private String contentcode;

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

    public String getPageindex() {
        return pageindex;
    }

    public void setPageindex(String pageindex) {
        this.pageindex = pageindex;
    }

    public String getPagesize() {
        return pagesize;
    }

    public void setPagesize(String pagesize) {
        this.pagesize = pagesize;
    }

    public String getContentcode() {
        return contentcode;
    }

    public void setContentcode(String contentcode) {
        this.contentcode = contentcode;
    }
}
