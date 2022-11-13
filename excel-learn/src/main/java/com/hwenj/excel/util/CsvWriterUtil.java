package com.hwenj.excel.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.csv.CsvWriter;

import java.util.Collection;
import java.util.Map;

/**
 * @author shiyuhou
 * @version 1.0
 * @description: csv输出工具类
 * @date 9/6/21 5:57 PM
 */
public class CsvWriterUtil {

    /**
     * @param csvWriter
     * @param beans       集合数据
     * @param isWriteHead 是否写入表头
     * @description: 将一个Bean集合写出到Writer，并自动生成表头
     * 适用于单线程
     * @return: void
     * @author shiyuhou
     * @date: 9/10/21 4:59 PM
     */
    public static void writeBeans(CsvWriter csvWriter, Collection<?> beans, boolean isWriteHead) {
        synchronized (CsvWriterUtil.class) {
            if (CollUtil.isNotEmpty(beans)) {
                Map<String, Object> map;
                for (Object bean : beans) {
                    map = BeanUtil.beanToMap(bean);
                    if (isWriteHead) {
                        csvWriter.writeLine(map.keySet().toArray(new String[0]));
                        isWriteHead = false;
                    }
                    csvWriter.writeLine(Convert.toStrArray(map.values()));
                }
                csvWriter.flush();
            }
        }
    }

    /**
     * @param csvWriter
     * @param bean      单行数据
     * @param isFirst
     * @description: 将一个Bean写出到Writer，并自动生成表头
     * 适用于单线程
     * @return: void
     * @author shiyuhou
     * @date: 9/10/21 4:59 PM
     */
    public static <T> void writeBean(CsvWriter csvWriter, T bean, boolean isFirst) {
        synchronized (CsvWriterUtil.class) {
            Map<String, Object> map;
            map = BeanUtil.beanToMap(bean);
            if (isFirst) {
                csvWriter.writeLine(map.keySet().toArray(new String[0]));
            }
            csvWriter.writeLine(Convert.toStrArray(map.values()));
            csvWriter.flush();
        }
    }

    /**
     * @param csvWriter
     * @param bean
     * @description: 根据bean的别名生成csv表头
     * @return: void
     * @author shiyuhou
     * @date: 9/10/21 4:59 PM
     */
    public static void writeHeaders(CsvWriter csvWriter, Object bean) {

        Map<String, Object> map = BeanUtil.beanToMap(bean);
        csvWriter.writeLine(map.keySet().toArray(new String[0]));
        csvWriter.flush();
    }
}
