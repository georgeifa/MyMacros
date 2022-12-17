package com.example.mymacros.Helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;

import androidx.core.app.NotificationCompat;

import com.example.mymacros.Activities.MainActivity;
import com.example.mymacros.R;



public class Notification_Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.dont_forget );
            String description = context.getString(R.string.its_important);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("MyMacros", name, importance);
            channel.setDescription(description);


            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

       Intent repeating_intent = new Intent(context, MainActivity.class);

        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

       PendingIntent pendingIntent = PendingIntent.getActivity(context,100,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "MyMacros")
                 .setSmallIcon(R.drawable.ic_notifications)
                 .setContentTitle(Html.fromHtml("<b><font font-size:17px>" +
                         context.getString(R.string.dont_forget) +
                         "</font><b>"))
                 .setContentText(Html.fromHtml("<i><font font-size:13px color=\"#FF757575\">" +
                         context.getString(R.string.its_important) +
                         " </font></i>"))
                 .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);




        builder.setContentIntent(pendingIntent);

        notificationManager.notify(100,builder.build());

    }
}
