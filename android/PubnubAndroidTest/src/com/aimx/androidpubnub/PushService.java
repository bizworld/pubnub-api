package com.aimx.androidpubnub;

import java.util.HashMap;

import org.json.JSONObject;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.AsyncTask.Status;

public class PushService extends Service {
    String PUSHCHANNEL = "c2dmalt";
    Pubnub pubnub;
    PushReceiver mPushReceiver = new PushReceiver();
    PushListener mPushListener = new PushListener();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        pubnub = new Pubnub("demo", // PUBLISH_KEY
                "demo", // SUBSCRIBE_KEY
                "demo", // SECRET_KEY
                "demo", // CIPHER_KEY
                true // SSL_ON?
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (mPushListener.getStatus() != Status.RUNNING) {
            mPushListener.execute(PUSHCHANNEL);
        }
    }

    class PushReceiver implements Callback {
        private void generateNotification(Context context, String title,
                String message, Uri uri) {
            int icon = 0;
            long when = System.currentTimeMillis();

            icon = R.drawable.notify_icon;

            Notification notification = new Notification(icon, message, when);

            notification.defaults = Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            // Launch new Intent to view a new contact added
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW, uri);
            PendingIntent contentIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, notificationIntent, 0);
            notification.setLatestEventInfo(getApplicationContext(), title,
                    message, contentIntent);

            NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(1, notification);

        }

        public boolean execute(JSONObject message) {
            try {
                generateNotification(getApplicationContext(),
                        message.getString("title"), message.getString("text"),
                        Uri.parse(message.getString("url")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
    }

    class PushListener extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            {
                try {
                	HashMap<String, Object> args = new HashMap<String, Object>(2);
                	args.put("channel", params[0]);
                	args.put("callback", mPushReceiver);
                    pubnub.subscribe(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return Boolean.TRUE; // Return your real result here
        }
    }
}
