package com.ubo.iptv.entity.gdgd;

import lombok.Data;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@Data
public class TypeKindESSearchDTO {
    Integer mediaType;
    Integer mediaKind;
    Integer isCharge;
    Integer mediaId;
    String mediaCode;
    Float score;
}
