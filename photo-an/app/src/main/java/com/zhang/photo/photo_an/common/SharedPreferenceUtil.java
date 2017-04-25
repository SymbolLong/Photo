package com.zhang.photo.photo_an.common;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by zhangsl on 2017/4/25.
 */
public class SharedPreferenceUtil {

    public static String getHost(Context context){
        return context.getSharedPreferences(Common.SPNAME,MODE_PRIVATE).getString(Common.HOST,Common.DEFAULT_HOST);
    }

    public static void setHost(Context context,String value){
        SharedPreferences  sp = context.getSharedPreferences(Common.SPNAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Common.HOST,value);
        editor.commit();
    }
    public static String getPort(Context context){
        return context.getSharedPreferences(Common.SPNAME,MODE_PRIVATE).getString(Common.PORT,Common.DEFAULT_PORT);
    }

    public static void setPort(Context context,String value){
        SharedPreferences  sp = context.getSharedPreferences(Common.SPNAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Common.PORT,value);
        editor.commit();
    }

    public static int getID(Context context){
        return context.getSharedPreferences(Common.SPNAME,MODE_PRIVATE).getInt(Common.ID,1);
    }

    public static void setID(Context context,int value){
        SharedPreferences  sp = context.getSharedPreferences(Common.SPNAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Common.ID,value);
        editor.commit();
    }




}
