package com;

import com.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hwenj
 * @since 2022/11/29
 */
public class TestSort {
    @Test
    public void test(){
        List<String> list = new ArrayList<>();
        list.add("2022-11-19 10:00:00");
        list.add("2022-11-29 10:00:00");
        list.add("2022-10-19 10:00:00");
        list.add("2022-12-19 10:00:00");
         List<String> collect = list.stream()
                .sorted((o1, o2) -> {
                    Date date1 = DateUtil.parseDate(o1, DateUtil.FULL_TIME_SPLIT_PATTERN);
                    Date date2 = DateUtil.parseDate(o2, DateUtil.FULL_TIME_SPLIT_PATTERN);
                    System.out.println("date1.getTime():"+date1.getTime() + "  date2.getTime:"+date2.getTime() + "  "+(date1.getTime() > date2.getTime()));
                    return date1.getTime() > date2.getTime() ? -1 : 1;
                })
                .collect(Collectors.toList());
        System.out.println(collect);
    }
}
