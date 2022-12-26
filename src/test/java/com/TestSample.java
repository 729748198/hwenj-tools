package com;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hwenj
 * @since 2022/11/16
 */
public class TestSample {

    @Test
    public void test(){
        String domainId = "8234,8221,43231,8345,1111";
        List<String> domainList = Arrays.stream(domainId.toString().split("\\,")).filter(e -> !"".equals(e)).collect(Collectors.toList());
        System.out.println(domainList.stream().count());
    }
}
