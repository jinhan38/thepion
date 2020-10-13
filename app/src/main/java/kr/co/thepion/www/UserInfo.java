package kr.co.thepion.www;

public class UserInfo {


    private static String isLogin = "";

    private static String autoLogin = "";

    private static String title = "n";
    private static String body = "n";
    private static String userNum = "n";
    private static String type = "n";
    private static String level = "";
    private static String loanNum = "";
    private static String ahNum = "";
    private static String needLogin = "";
    private static Object pushUrl = "";


    public static String getNeedLogin() {
        return needLogin;
    }

    public static void setNeedLogin(Object needLogin) {
        UserInfo.needLogin = String.valueOf(needLogin);
    }

    public static Object getPushUrl() {
        return pushUrl;
    }

    public static void setPushUrl(Object pushUrl) {
        UserInfo.pushUrl = pushUrl;
    }

    public static String getAhNum() {
        return ahNum;
    }

    public static void setAhNum(Object ahNum) {
        UserInfo.ahNum = String.valueOf(ahNum);
    }

    private static boolean getPushClick = false;



    public static boolean isGetPushClick() {
        return getPushClick;
    }

    public static void setGetPushClick(boolean getPushClick) {
        UserInfo.getPushClick = getPushClick;
    }

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        UserInfo.title = title;
    }

    public static String getBody() {
        return body;
    }

    public static void setBody(String body) {
        UserInfo.body = body;
    }



    public static String getUserNum() {
        return userNum;
    }

    public static void setUserNum(String userNum) {
        UserInfo.userNum = userNum;
    }

    public static String getIsLogin() {
        return isLogin;
    }

    public static void setIsLogin(String isLogin) {
        UserInfo.isLogin = isLogin;
    }

    public static String getType() {
        return type;
    }

    public static void setType(Object type) {
        UserInfo.type = String.valueOf(type);
    }

    public static String getLevel() {
        return level;
    }

    public static void setLevel(Object level) {
        UserInfo.level = String.valueOf(level);
    }

    public static String getLoanNum() {
        return loanNum;
    }

    public static void setLoanNum(Object loanNum) {
        UserInfo.loanNum = String.valueOf(loanNum);
    }

    public static String getAutoLogin() {
        return autoLogin;
    }

    public static void setAutoLogin(String autoLogin) {
        UserInfo.autoLogin = autoLogin;
    }
}
