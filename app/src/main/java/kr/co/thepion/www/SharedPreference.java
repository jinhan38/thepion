package kr.co.thepion.www;

import android.content.Context;
import android.preference.PreferenceManager;

public class SharedPreference {

    private static final String ISLOGIN = "isLogin";
    private static final String LEVEL = "level";
    private static final String AUTOLOGIN = "autoLogin";

    public static android.content.SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setIsLogin(String isLogin){
        android.content.SharedPreferences.Editor editor = getSharedPreferences(MainActivity.mainActivity).edit();
        editor.putString(ISLOGIN, isLogin);
        editor.apply();
    }

    public static String getIsLogin(){
        return getSharedPreferences(MainActivity.mainActivity).getString(ISLOGIN, "");
    }

    public static void setLevel(String level){
        android.content.SharedPreferences.Editor editor = getSharedPreferences(MainActivity.mainActivity).edit();
        editor.putString(LEVEL, level);
        editor.apply();
    }

    public static String getLevel(){
        return getSharedPreferences(MainActivity.mainActivity).getString(LEVEL, "");
    }

    public static void setAutologin(String autoLogin){
        android.content.SharedPreferences.Editor editor = getSharedPreferences(MainActivity.mainActivity).edit();
        editor.putString(AUTOLOGIN, autoLogin);
        editor.apply();
    }

    public static String getAutologin(){
        return getSharedPreferences(MainActivity.mainActivity).getString(AUTOLOGIN, "");
    }




}
