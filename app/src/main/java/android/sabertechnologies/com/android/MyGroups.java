package android.sabertechnologies.com.android;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class MyGroups extends AppCompatActivity {
    ArrayList<String> a;
    ArrayAdapter<String> ada;
    ArrayList<String> ai;
    ListView lv,lv1;
    DB myDB;
    String use;
    DatabaseReference db;
    Ada ia;

    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sh = getSharedPreferences("themePref" , MODE_PRIVATE);
        String str = sh.getString("th" , "gra");
        setTheme(R.style.gra);
        switch(str) {
            case "gr":
                setTheme(R.style.gr);
                break;
            case "bl":
                setTheme(R.style.bl);
                break;
            case "gra":
                setTheme(R.style.gra);
                break;
            case "ora":
                setTheme(R.style.ora);
                break;
            case "red":
                setTheme(R.style.red);
                break;
            default:
                setTheme(R.style.gra);
                break;

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        this.lv = (ListView) findViewById(R.id.l);
        lv1 = (ListView) findViewById(R.id.lv1);
        this.myDB = new DB(getApplicationContext());
        this.a = new ArrayList();
        ai = new ArrayList<String>();
        db = FirebaseDatabase.getInstance().getReference();
        this.ada = new ArrayAdapter(getApplicationContext(), R.layout.tvforlv, this.a);
        ia = new Ada(getApplicationContext() , ai);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        lv1.setAdapter(ia);
        this.lv.setAdapter(this.ada);

        Cursor show = this.myDB.viewResults();
        if (show.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "Please Create / Join In Any Chat Group First", Toast.LENGTH_LONG).show();
        }
        while (show.moveToNext()) {
            this.a.add(show.getString(1));
            this.ada.notifyDataSetChanged();
        }
        this.lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String key = MyGroups.this.lv.getItemAtPosition(i).toString();
                Intent intent = new Intent(MyGroups.this.getApplicationContext(), ChatRoom.class);
                intent.putExtra("Key", key);
                MyGroups.this.startActivity(intent);
                finish();
            }
        });

        //FirebaseDatabase.getInstance().getReference().child("UserOnline").child(sharedPreferences.getString("Key", "None")).setValue(0)

        lv1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = ai.get(i);
                Intent inte = new Intent(getApplicationContext(),ImgGCM.class);
                inte.putExtra("gpi" , s);
                inte.putExtra("ke" , a.get(i));
                startActivity(inte);
                finish();
            }
        });
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                int index = lv.getFirstVisiblePosition();
                View v = lv.getChildAt(0);
                int top = (v == null)? 0: v.getTop();
                lv1.setSelectionFromTop(index, top);
            }
        });

        lv1.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true; // Indicates that this has been handled by you and will not be forwarded further.
                }
                return false;
            }
        });

        SharedPreferences s = getSharedPreferences("Pref" , MODE_PRIVATE);
        use = s.getString("Key" , "None");

        db.child("Unread").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.getKey().equals(use) && !foregrounded()){

                    Intent inte = new Intent(getApplicationContext() , ReciveNoti.class);
                    inte.putExtra("user" , use);
                    boolean alrm = (PendingIntent.getBroadcast(getApplicationContext() , 0 , inte , PendingIntent.FLAG_NO_CREATE) != null);

                    if (!alrm){
                        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext() , 0 , inte , 0);
                        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP , SystemClock.elapsedRealtime() , 1000 , pi);
                    }

                }else{
                    db.child("Unread").child(use).removeValue();
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

        db.child("GrpImgs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String url = dataSnapshot.getValue(String.class);
                String usr = dataSnapshot.getKey().toString();
                int num1 = 0;

                for(int i = 0 ; i < usr.length() ; i++){
                    if(usr.charAt(i) == '_'){
                        num1 = i+1;
                        break;
                    }
                }

                if(a.contains(usr.substring(num1))){
                    ai.add(url);
                    ia.notifyDataSetChanged();
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

    public void create(View v) {
        startActivity(new Intent(getApplicationContext(), Create.class));
        finish();
    }

    public void join(View v) {
        startActivity(new Intent(getApplicationContext(), Join.class));
        finish();
    }

    public void saberbot(View v) {
        Intent i = new Intent(getApplicationContext(), Saberbot.class);
        startActivity(i);
        finish();
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

    @Override
    public void onBackPressed() {
        finish();
    }



}


