package com.hwenj.excel.bean;

import cn.hutool.core.annotation.Alias;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 *@Description
 *@Author zhoujun
 *@Date 2022/7/28
 */

@Data
@Accessors(chain = true)
@ToString
public class CluesCount {
    @Alias("序号")
    private Integer sn;
    @Alias("省份")
    private String province;
    @Alias("行业")
    private String industry;
    @Alias("被检查单位名称")
    private String company;
    @Alias("专项包编号")
    private String specialNum;
    @Alias("网络区域")
    private String deploymentLocation;
    @Alias("IOC命中数量（条）")
    private Integer iocNum;
    @Alias("APT线索命中数量（条）")
    private Integer aptNum;
    @Alias("黑客攻击等违法犯罪网络攻击线索数量（条）")
    private Integer blackNum;

}
