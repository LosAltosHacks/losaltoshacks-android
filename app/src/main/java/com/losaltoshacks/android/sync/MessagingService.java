package com.losaltoshacks.android.sync;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.losaltoshacks.android.MainActivity;
import com.losaltoshacks.android.R;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null ) {
            Log.d("MessagingService", "Notification received");

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            String title = remoteMessage.getNotification().getTitle();
            if (title == null || title.equals("")) title = getString(R.string.app_name);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_ic_notification)
                    .setContentTitle(title)
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))
                    .setContentIntent(pendingIntent);

            NotificationManager notifactionManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notifactionManager.notify(0, builder.build());
        }
    }
}
