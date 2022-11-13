package com.hwenj.es.learn;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import sun.security.util.BitArray;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 获取历史库 有效历史记录工具类
 *
 * @author hwenj
 * @since 2022/9/27
 */
public class T01HistoryValidDateUtil {

    public static void main(String[] args) {


        System.out.println(byteArrayToString(intToBytes(268435456)));
        //有效天1
//        int validDay9 = getDayInt("2022-09-01");
//        System.out.println("validDay9:" + validDay9);
//        validDay9 |= getDayInt("2022-09-27");
//        System.out.println("validDay9:" + validDay9);
//        validDay9 |= getDayInt("2022-09-28");
//        System.out.println("validDay9:" + validDay9);
//        validDay9 |= getDayInt("2022-09-29");
//        System.out.println("validDay9:" + validDay9);
//        validDay9 |= getDayInt("2022-09-30");
//        System.out.println("validDay9:" + validDay9);
//        System.out.println("validDay9:" + byteArrayToString(intToBytes(validDay9)));
    }

    /**
     * 初始化历史记录日期数组
     *
     * @param today 今天的数字
     * @param month 本月的数字
     * @return 日期数组
     */
    public static Integer[] initArray(Integer today, Integer month) {
        Integer[] array = new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        array[month] = today;
        return array;
    }

    public static Integer[] initArray() {
        return new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    public static int getDayInt(String dateStr) {
        DateTime today = DateUtil.parse(dateStr);
        int currentDay = today.dayOfMonth();
        System.out.println("currentDay" + currentDay);
        return (int) Math.pow(2, (currentDay - 1));
    }


    public static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(x);
        return buffer.array();
    }

    public static String byteArrayToString(byte[] array) {
        BitArray bitArray = new BitArray(array.length * 8, array);
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = bitArray.length() - 1; i >= 0; i--) {
            if (bitArray.get(i)) {
                stringBuffer.append("1");
            } else {
                stringBuffer.append("0");
            }
        }
        return stringBuffer.toString();
    }
}
