package com.hwenj.es.learn;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import sun.security.util.BitArray;

import java.nio.ByteBuffer;

/**
 * @author cwq
 * @create 2022-09-27
 */

@Slf4j
public class ValidDate {

    public static void main(String[] args) {
        //有效天1
        int validDay9 = getDayInt("2022-07-01");

        validDay9 = 0 | validDay9;
        System.out.println("validDay9:" + byteArrayToString(intToBytes(validDay9)));

        System.out.println("validDay9:" + validDay9);
        validDay9 |= getDayInt("2022-07-27");
        System.out.println("validDay9:" + validDay9);
        validDay9 |= getDayInt("2022-07-28");
        System.out.println("validDay9:" + validDay9);
        validDay9 |= getDayInt("2022-07-29");
        System.out.println("validDay9:" + validDay9);

        validDay9 |= getDayInt("2022-07-31");
        System.out.println("validDay9:" + validDay9);
        System.out.println("validDay9:" + byteArrayToString(intToBytes(validDay9)));

    }




    public static int getDayInt(String dateStr) {
        DateTime today = DateUtil.parse(dateStr);
        int currentDay = today.dayOfMonth();
        // System.out.println(currentDay);
        return (int) Math.pow(2, (currentDay - 1));
    }


    public static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(x);
        return buffer.array();
    }

    public static String byteArrayToString(byte[] array) {
        BitArray bitArray = new BitArray(array.length * 8, array);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = bitArray.length() - 1; i >= 0; i--) {
            if (bitArray.get(i)) {
                stringBuffer.append("1");
            } else {
                stringBuffer.append("0");
            }
        }
        return stringBuffer.toString();
    }


    public static int[] initArray() {
        return new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    public static int[] initArray(Integer month, Integer todayInt) {
        int[] validDate = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        validDate[month] = todayInt;
        return validDate;
    }
}
