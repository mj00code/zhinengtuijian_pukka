package cn.pukkasoft.datasync.model;

import java.util.List;

/**
 * @Author SHAWN LIAO
 * @ClassName ContentPageResult
 * @Date 2020/11/23 14:40
 * @Description 媒资详情列表
 */
public class ContentPageResult {
    private String resultcode;
    private String resultdesc;
    private int pageindex;
    private int pagesize;
    private int pagetotal;
    private int sizetotal;
    private String errorCode;
    private String clientIp;
    private List<Contentinfo> result;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getResultdesc() {
        return resultdesc;
    }

    public void setResultdesc(String resultdesc) {
        this.resultdesc = resultdesc;
    }

    public int getPageindex() {
        return pageindex;
    }

    public void setPageindex(int pageindex) {
        this.pageindex = pageindex;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public int getPagetotal() {
        return pagetotal;
    }

    public void setPagetotal(int pagetotal) {
        this.pagetotal = pagetotal;
    }

    public int getSizetotal() {
        return sizetotal;
    }

    public void setSizetotal(int sizetotal) {
        this.sizetotal = sizetotal;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public List<Contentinfo> getResult() {
        return result;
    }

    public void setResult(List<Contentinfo> result) {
        this.result = result;
    }
}
