package com.ubo.iptv.entity.gdgd;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author
 * @since 2020-10-26
 */
@Data
public class ClickFilterDTO {

    private String userId;

    private String sceneId;

    private String mediaType;

    private String mediaId;
    
    private String date;
}
