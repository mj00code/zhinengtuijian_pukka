package cn.pukkasoft.datasync.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * User
 * @author TODO:
 *
 */
@Data
@EqualsAndHashCode
@JsonIgnoreProperties(value = { "handler" })
public class User implements java.io.Serializable{
	
	/**id*/
    private Integer id;
	/**serverId*/
    private String serverId;
	/**phone*/
    private String phone;
	/**timestamp*/
    private String timestamp;
	/**status*/
    private String status;
	/**cityNum*/
    private String cityNum;
	/**areaNum*/
    private String areaNum;
	/**channelNum*/
    private String channelNum;
	/**juXiangNum*/
    private String juXiangNum;
	/**peopleNum*/
    private String peopleNum;
	/**spnum*/
    private String spnum;
	/**opnum*/
    private String opnum;
	/**deviceNum*/
    private String deviceNum;
	/**correlateId*/
    private String correlateId;
	/**ottuserId*/
    private String ottuserId;
	/**iptvuserId*/
    private String iptvuserId;
	/**password*/
    private String password;
	/**sn*/
    private String sn;
	/**createtime*/
    private String createtime;
	/**updatetime*/
    private String updatetime;
	/**token*/
    private String token;
	/**用户分组id*/
    private String userGroupId;
	/**用户分组名称*/
    private String userGroupName;
	/**平台标示1：移动2：联通*/
    private Integer platform;
	/**激活时间*/
    private String activetime;
	/**地市号名称*/
    private String cityNumName;
}
