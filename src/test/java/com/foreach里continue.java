package com;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author hwenj
 * @since 2022/11/24
 */
public class foreach里continue {
    @Test
    public void test(){
        String[] ts= new String[]{"123","456","789"};
        List<String> list = Arrays.asList(ts);
        list.forEach(t->{
            if(t.equals("123")){
                return;
            }
            System.out.println(t);
        });
    }
}
