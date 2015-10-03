package com.example.matsumotokazuya.mynidonealarm;

/**
 * Created by matsumotokazuya on 2015/10/03.
 */
public class ParseUtil {
    public static int ParseStringToInt(String value){
        int rVal = Integer.parseInt(value);
        return rVal;
    }

    public static String ParseIntToString(int value){
        String rVal = String.valueOf(value);
        return  rVal;
    }


}
