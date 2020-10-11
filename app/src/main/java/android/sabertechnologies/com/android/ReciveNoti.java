package android.sabertechnologies.com.android;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by madhav on 11/7/18.
 */

public class ReciveNoti extends BroadcastReceiver {

    DatabaseReference db;
    String usern;
    Context con;

    @Override
    public void onReceive(Context context, Intent intent) {

        usern = intent.getStringExtra("user");

        db = FirebaseDatabase.getInstance().getReference();

        con = context;

        db.child("Unread").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.getKey().equals(usern) && !foregrounded()){

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        makeNotificationChannel("CHANNEL_1", "Example channel", NotificationManager.IMPORTANCE_DEFAULT);
                    }

                    NotificationCompat.Builder n = new NotificationCompat.Builder(con,"CHANNEL_1");
                    n.setSmallIcon(R.drawable.logo);
                    n.setContentTitle("Unread Messages");
                    n.setContentText("You Have Unread Messages , Open Chatterbox To View Them");
                    n.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat not = NotificationManagerCompat.from(con);

                    not.notify(1 , n.build());

                    db.child("Unread").child(usern).removeValue();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public boolean foregrounded() {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(appProcessInfo);
            return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
        }
        else{
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel(String id, String name, int importance)
    {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive

        NotificationManager notificationManager = (NotificationManager)con.getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }
}
