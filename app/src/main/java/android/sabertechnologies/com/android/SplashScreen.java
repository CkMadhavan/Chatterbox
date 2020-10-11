package android.sabertechnologies.com.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences s;
    String str;
    RelativeLayout relativeLayout;
    DB database;

    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sh = getSharedPreferences("themePref" , MODE_PRIVATE);
        String stri = sh.getString("th" , "gra");
        setTheme(R.style.gra);
        switch(stri) {
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
        setContentView(R.layout.activity_splash_screen);
        this.s = getSharedPreferences("Pref", 0);
        this.str = this.s.getString("Key", "None");
        database = new DB(getApplicationContext());
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        relativeLayout.setVisibility(View.INVISIBLE);
        DatabaseReference d = FirebaseDatabase.getInstance().getReference().child("AddRequests");
        d.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String usrindb = dataSnapshot.getValue(String.class).split("-")[0];
                String ks = dataSnapshot.getKey();
                String tbak = dataSnapshot.getValue(String.class).split("-")[1];
                if (usrindb.equals(str)) {
                    database.addTask(tbak);
                    SharedPreferences share = getSharedPreferences(tbak , MODE_PRIVATE);
                    SharedPreferences.Editor e = share.edit();
                    e.putString(tbak , ks);
                    e.commit();
                    FirebaseDatabase.getInstance().getReference().child("AddRequests").child(ks).removeValue();
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
        FirebaseDatabase.getInstance().getReference().child("Versions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE){
                    Toast.makeText(getApplicationContext() , "Updates Available" , Toast.LENGTH_LONG).show();
                    relativeLayout.setVisibility(View.VISIBLE);
                }else{
                    relativeLayout.setVisibility(View.INVISIBLE);
                    new Thread() {
                        public void run() {
                            try {
                                sleep(1500);
                                if (SplashScreen.this.str != "None") {
                                    SplashScreen.this.startActivity(new Intent(SplashScreen.this.getApplicationContext(), MyGroups.class));
                                    SplashScreen.this.finish();
                                    return;
                                }
                                SplashScreen.this.startActivity(new Intent(SplashScreen.this.getApplicationContext(), First.class));
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void up(View v){
        Intent intention = new Intent(Intent.ACTION_VIEW , Uri.parse("https://chatterboxandroid.wordpress.com/"));
        startActivity(intention);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
