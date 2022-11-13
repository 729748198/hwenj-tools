package com.hwenj.excel.bean;

/**
 *@Description
 *@Author zhoujun
 *@Date 2022/7/11
 */

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author shiyuhou
 * @desc SL2022情报库实体对象
 * @date 2022/7/5
 **/
@Getter
@Setter
@Accessors(chain = true)
public class IntelligenceFinalRaw {

    private Integer id;

    private String ioc;

    private String port;

    private String ioc_type;

    private String risk_level;

    private String confidence;

    private String ioc_direction;

    private String ioc_tag;

    private String category_final;

    private String type;

    private String tpd;

    private String dns_analysis;

    private String domain_ips;

    private String apt_org_final;

    private String apt_country;

    private String ip_country;

    private String ip_city;

    private String platform;

    private String description;

    private String context;

    private String status;

    private String ctime;

    private String utime;

    private String etime;

    private String ioc_orig;

    private String ioc_fileHash;

    private String ioc_target;

    private String ioc_link;

    private String ioc_conformrule;

    private String ioc_industry;

    private String family;

    private String ioc_org;

    private String ioc_org_id;


    public String getIoc_orig() {
        switch (this.ioc_org_id) {
            case "1":
                this.ioc_orig = "安天";
                break;
            case "2":
                this.ioc_orig = "奇安信";
                break;
            case "3":
                this.ioc_orig = "360";
                break;
            case "4":
                this.ioc_orig = "微步在线";
                break;
            case "5":
                this.ioc_orig = "神州网云";
                break;
            case "6":
                this.ioc_orig = "阿里";
                break;
            case "7":
                this.ioc_orig = "信工所";
                break;
            case "8":
                this.ioc_orig = "360";
                break;
            case "9":
                this.ioc_orig = "奇安信";
                break;
            case "10":
                this.ioc_orig = "腾讯";
                break;
            case "11":
                this.ioc_orig = "G01正向";
                break;
            case "12":
                this.ioc_orig = "启明星辰";
                break;
            case "13":
                this.ioc_orig = "天融信";
                break;
            case "14":
                this.ioc_orig = "绿盟";
                break;
            case "15":
                this.ioc_orig = "亚信";
                break;
            case "16":
                this.ioc_orig = "腾讯";
                break;
            case "17":
                this.ioc_orig = "华为";
                break;
            case "18":
                this.ioc_orig = "青藤云";
                break;
            case "19":
                this.ioc_orig = "深信服";
                break;
            case "20":
                this.ioc_orig = "安恒";
                break;
            case "21":
                this.ioc_orig = "恒安嘉新";
                break;
            case "22":
                this.ioc_orig = "默安";
                break;
            case "23":
                this.ioc_orig = "白帽汇";
                break;
            case "24":
                this.ioc_orig = "中睿天下";
                break;
            case "25":
                this.ioc_orig = "火绒";
                break;
            case "26":
                this.ioc_orig = "知道创宇";
                break;
            case "27":
                this.ioc_orig = "科来";
                break;
            case "28":
                this.ioc_orig = "远江盛邦";
                break;
            case "29":
                this.ioc_orig = "安芯网盾";
                break;
            case "":
                break;
            default:
                break;
        }
        return ioc_orig;
    }

}
