package com.ubo.iptv.manage.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TagRate {
    private LocalDateTime date;
    private Integer allCount;
    private Integer count;
    private BigDecimal rate;
    private Long tagId;
    private String tagName;
}
