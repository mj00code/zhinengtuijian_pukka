package com.ubo.iptv.core;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangjian
 * @Date 2017/11/3
 * @Desc
 */
@Data
public class PageResponse<T> extends PageImpl<T> {

    @ApiModelProperty(value = "当前页", example = "0")
    private int page;
    @ApiModelProperty(value = "每页数量", example = "10")
    private int size;
    @ApiModelProperty(value = "总数", example = "100")
    private long total;
    @ApiModelProperty(value = "是否是首页", example = "true")
    private boolean first;
    @ApiModelProperty(value = "是否是尾页", example = "true")
    private boolean last;
    @ApiModelProperty(value = "是否有下一页", example = "true")
    private boolean hasNext;
    @ApiModelProperty(value = "是否有下一页", example = "true")
    private boolean hasPrevious;
    private int totalPages;
    private long totalElements;
    private int number;
    private int numberOfElements;

    public PageResponse(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
        this.numberOfElements = super.getNumberOfElements();
        this.number = super.getNumber();
        this.page = super.getNumber();
        this.size = super.getSize();
        this.total = super.getTotalElements();
        this.totalElements = super.getTotalElements();
        this.totalPages = super.getTotalPages();
        this.hasNext = super.hasNext();
        this.hasPrevious = super.hasPrevious();
        this.first = super.isFirst();
        this.last = super.isLast();
    }

    public PageResponse() {
        super(new ArrayList<T>());
    }

    public PageResponse(List content) {
        this(content, null, null == content ? 0L : (long) content.size());
    }
}
