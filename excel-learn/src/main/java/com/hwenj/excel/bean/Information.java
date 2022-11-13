package com.hwenj.excel.bean;

import cn.hutool.core.annotation.Alias;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

/**
 *@Description
 *@Author zhoujun
 *@Date 2022/7/12
 */

@Data
@Accessors(chain = true)
@ToString
public class Information {

    @Alias("被检查单位编号")
    private String sn ;

    @Alias("实际检查单位名称")
    private String company ;

    @Alias("实际检查单位名称(全称)")
    private String company1 ;

    @Alias("省份")
    private String province;

    @Alias("行业")
    private String industry;

    @Alias("地区")
    private String area;

    @Alias("设备部署网络位置")
    private String deploymentLocation;

    public String getCompany() {
        return StringUtils.hasText(company)? company : company1;
    }
}
