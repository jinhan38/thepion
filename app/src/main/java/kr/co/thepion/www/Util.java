package kr.co.thepion.www;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

public class Util extends Activity {

    public static String NUM = "";
    public static String MID = "";
    public static String MEMBER_LEVEL = "";
    public static String APP_PACKAGENAME = "kr.co.thepion.www";
    public static String APP_PACKAGENAME_FOR_MANAGER = "kr.co.pionmanager.www";
    public static String pionLoginUrl = "https://www.thepion.co.kr:444/Mobile/m_Views/Login/Login_m";
    public static String isPushOpen = "y";



//    public static String SERVICEKEY = "cc01t3rJ1v0:APA91bHwP_2vGu-SjKmF6OlXl1Zztdo_tRMxo6-u3WwPwjxG_3GBojqgeCg_dcoGJp1HACWK24cuboEwl-2s9U8DGXrKlzbwem_ni8qaCUQZzgPPyjiici2xf1fNp7jeqQtegFOPDlVt";
    public static String TOKEN = "";
    public static String VERSION = "";
    public static boolean isLogin = false;

    public static Context CURRENT_CONTEXT = null;
    public static boolean SETTING_PUSH_ALL = false;

    //파일 다운로드
    public static String DOWNLOAD_FILE_NAME = "";



    public static void savePhone(String name, String phone){
        Log.e("Util.savePhone", "진입");
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra( ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        Util.CURRENT_CONTEXT.startActivity(intent);
    }


    public static void callInWebView(String phone){
        String tel_number = "tel:" + phone;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tel_number));
        Util.CURRENT_CONTEXT.startActivity(intent);
    }

}
