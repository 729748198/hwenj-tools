package com;

/**
 * @author hwenj
 * @since 2022/9/9
 */
public class TestRegex {
    public static void main(String[] args) {
        String s = "我军发展史上第一次抉择：革命主力军是工人还是农民？\n" +
                "\n" +
                "在我军发展的萌芽期";
        System.out.println(s.replaceAll("\\s*|\r|\n|\t",""));
    }
}
