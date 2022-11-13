package com.hwenj.es.learn;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Person {

    public String personId;
    public String name;
    public String number;
    public transient String account;
}