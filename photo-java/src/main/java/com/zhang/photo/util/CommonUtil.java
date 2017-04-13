package com.zhang.photo.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangsl on 2017/4/13.
 */
public class CommonUtil {

    private static List<String> suffix = new ArrayList<>();

    static {
        suffix.add(".png");
        suffix.add(".jpg");
        suffix.add(".jpeg");
    }

    public static boolean isPicture(String name){
        for (String s : suffix) {
            if (name.endsWith(s)){
                return true;
            }
        }
        return false;
    }
}
