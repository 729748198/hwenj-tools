package com.hwenj.excel.bean;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author shiyuhou
 * @version 1.0
 * @description: 外联告警
 * hitType-IP情报   ioc=目的IP
 * hitType-域名情报   ioc=外联域名/URL
 * hitType-URL情报   ioc=外联域名/URL 或 主机名
 * 1.归档文件中命中类型为IP情报时 infoname取目的IP字段 <匹配的是情报离线包中的IP情报>
 * 2.归档文件中命中类型为域名情报时 infoname取外联域名/URL字段 <匹配的是情报离线包这种的域名情报>
 * 3.归档文件中命中类型为URL情报时（HTTP协议） infoname可能是外联域名/URL字段<匹配情报离线包中的URL情报> 也可能是host(新增)字段<匹配情报离线包中的域名情报>
 * @date 8/13/21 2:47 PM
 */
@Data
@Accessors(chain = true)
@ToString
public class OutLog {

    @Alias("日志类型")
    private Integer logType = -1;

    @Alias("单位编号")
    private String company;
    @Alias("外联目标")
    private String externalIoc = "";

    // 2021-08-19 11:11:59
    @Alias("日志时间")
    private String time;
    @JsonIgnore
    @Alias("\uFEFF时间")
    private String time1;
    @JsonIgnore
    @Alias("时间")
    private String time2;

    public String getTime() {
        return StringUtils.hasText(time1) ? time1 : time2;
    }

    // 114.242.143.169 受控设备IP
    @Alias("受控设备")
    private String srcIp;
    // -- / 具体端口
    @Alias("源端口")
    private String srcPort;
    // -- / 具体外联域名/URL
    @Alias("外联域名/URL")
    private String destAddress;
    @Alias("主机名")
    private String host;
    // -- / 具体DNS
    @Alias("下一跳DNS")
    private String nextDns;

    // -- / 具体目的IP
    @Alias("解析目的IP")
    private String destIp;
    @JsonIgnore
    @Alias("目的ip")
    private String destIp1;
    @JsonIgnore
    @Alias("目的 IP")
    private String destIp2;
    @JsonIgnore
    @Alias("目的IP")
    private String destIp3;

    public String getDestIp() {
        return StringUtils.hasText(destIp1) ? destIp1 : (StringUtils.hasText(destIp2) ? destIp2 : destIp3);
    }

    // -- / 具体目的端口
    @Alias("目的端口")
    private String destPort;
    // APT|关联分析情报
    @Alias("目标类型")
    private String destType;
    // IP情报、域名情报、URL情报
    @Alias("命中类型")
    private String hitType;
    // 一所情报包、专项情报包
    @Alias("情报来源")
    private String sources;
    // 8|9|10
    @Alias("情报源编码")
    private String sourceNums;
    // 情报命中、结合端口研判
    @Alias("备注")
    private String hitRemark;

    /**
     * 自定义字段
     */
    @Alias("外联次数")
    private Integer externalCount = 0;
    @JsonFormat(pattern =  "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Alias("首次外联时间")
    private Date firstExternalTime;
    @JsonFormat(pattern =  "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Alias("最后外联时间")
    private Date endExternalTime;
}