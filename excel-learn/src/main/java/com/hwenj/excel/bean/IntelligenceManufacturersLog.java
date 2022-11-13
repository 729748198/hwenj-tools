package com.hwenj.excel.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 情报厂商线索处理日志
 * @Author shiyuhou
 * @Date 2022/7/17
 * @Version 1.0.0
 **/
@Getter
@Setter
@Accessors(chain = true)
@TableName("intelligence_manufacturers_log")
public class IntelligenceManufacturersLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单位名称
     */
    @TableField("company")
    private String company;

    /**
     * 单位编号
     */
    @TableField("company_no")
    private String companyNo;

    /**
     * 厂商名称
     */
    @TableField("supplier")
    private String supplier;

    /**
     * 厂商编号
     */
    @TableField("supplier_no")
    private String supplierNo;

    /**
     * 日志类型：1-线索文件生成，2-线索文件下载，3-线索误报回传
     */
    @TableField("log_type")
    private Integer logType;

    /**
     * 线索类型：1-apt情报，2-黑灰产情报，3-无误报
     */
    @TableField("clues_type")
    private Integer cluesType;

    /**
     * 线索文件路径
     */
    @TableField("clues_file_path")
    private String cluesFilePath;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;


}
