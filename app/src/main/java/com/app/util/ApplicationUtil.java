package com.app.util;

import android.content.Context;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class ApplicationUtil {
    public static String getProperties(Context ctx,String key){
        try {
            Properties prop = new Properties();
            InputStream in = ctx.getAssets().open("app.properties");         //打开assets目录下的config.properties文件
            prop.load( new InputStreamReader(in,"utf-8"));
            return prop.getProperty(key);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return "";
    }
}
