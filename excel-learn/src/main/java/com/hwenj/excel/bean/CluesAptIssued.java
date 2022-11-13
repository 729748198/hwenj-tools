package com.hwenj.excel.bean;

/**
 *@Description
 *@Author zhoujun
 *@Date 2022/7/11
 */

import cn.hutool.core.annotation.Alias;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author shiyuhou
 * @desc APT类线索下发 实体对象
 * @date 2022/7/5
 **/
@Data
@Accessors(chain = true)
@ToString
public class CluesAptIssued {

    @Alias("序号")
    private Integer sn;
    @Alias("被检查单位省份")
    private String province;
    @Alias("所属行业")
    private String industry;
    @Alias("被检查单位名称")
    private String company;
    @Alias("网络区域")
    private String deploymentLocation;
    @Alias("APT组织名称")
    private String aptOrgName;
    @Alias("APT组织归属国家或地区")
    private String aptOrgLocation;
    @Alias("受控主机地址")
    private String srcHost;
    @Alias("受控主机MAC")
    private String srcMac;
    @Alias("源端口")
    private String srcPort;
    @Alias("命中情报（IP/域名/URL）")
    private String ioc;
    @Alias("目的端口")
    private String destPort;
    @Alias("情报类型 ")
    private String ioc_type;
    @Alias("情报IP地址归属国家")
    private String country;
    @Alias("情报IP地址归属地区")
    private String region;
    @Alias("影响平台")
    private String platform;
    @Alias("情报描述信息")
    private String description;
    @Alias("威胁等级")
    private String riskLevel;
    @Alias("置信度")
    private String confidence;
    @Alias("外联地址存活状态")
    private String statusSurvival;
    @Alias("情报创建时间")
    private String createTime;
    @Alias("情报更新时间")
    private String updateTime;
    @Alias("情报过期时间")
    private String expireTime;
    @Alias("情报来源")
    private String iocOrig;
    @Alias("情报关联样本")
    private String samples;
    @Alias("攻击意图")
    private String attackIntention;
    @Alias("参考链接")
    private String iocLink;
    @Alias("关联恶意代码家族")
    private String family;
    @Alias("外联次数")
    private Integer externalCount;
    @Alias("首次外联时间")
    private String firstExternalTime;
    @Alias("最后外联时间")
    private String endExternalTime;
    @Alias("情报归属厂商")
    private String vendor;
}
