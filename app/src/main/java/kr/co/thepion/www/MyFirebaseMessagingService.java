package kr.co.thepion.www;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        PushUtils.acquireWakeLock(getApplicationContext());
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "onMessageReceived: ");
//        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getData());
        }
    }


    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);
    }


    private void sendNotification(String messageTitle, String messageBody, Map<String, String> data) {

        if (data != null) {
            if (data.get("url") != null) UserInfo.setPushUrl(data.get("url"));
            else UserInfo.setPushUrl("");
            Log.e(TAG, "sendNotification: url : " + UserInfo.getPushUrl());
            UserInfo.setLevel(data.get("level"));
            Log.e(TAG, "sendNotification: 파이어베이스 레벨 : " + data.get("level") );
            UserInfo.setType(data.get("type"));
            UserInfo.setNeedLogin(data.get("needLogin"));
            UserInfo.setGetPushClick(true);
        }


//        if (UserInfo.getNeedLogin().equals("y")) {
//            //로그인이 필요한지 아닌지 확인
//            Log.e(TAG, "sendNotification: 로그인 필요성 체크" );
//
//            if (SharedPreference.getIsLogin().equals("y")) {
//                Log.e(TAG, "sendNotification: 로그인 여부 체크" );
//
//
//                if (SharedPreference.getLevel().equals(UserInfo.getLevel())) {
//                    //로그인이 된 상태에서 레벨 체크
//                    Log.e(TAG, "sendNotification: 레벨체크 통과" );


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.appName);

        PushUtils.releaseWakeLock();

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.pion_icon)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(notificationBuilder);
        style.bigText(messageBody);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = getString(R.string.appName);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakelock.acquire(5000);
        notificationManager.notify(0, notificationBuilder.build());


//                } else {
//                    Log.e(TAG, "sendNotification: 레벨체크 통과 못함" );
//
//                }
//
//
//            }
//    }

    }


}


@SuppressLint("SpecifyJobSchedulerIdRange")
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class MyJobService extends JobService {

    private static final String TAG = "MyJobService";

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");

        PowerManager powerManager;
        PowerManager.WakeLock wakeLock;
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKELOCK");
        wakeLock.acquire(); // WakeLock 깨우기
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}

class PushUtils {
    private static PowerManager.WakeLock mWakeLock;

    @SuppressLint("InvalidWakeLockTag")
    public static void acquireWakeLock(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKEUP");
        mWakeLock.acquire();
    }

    public static void releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
}

