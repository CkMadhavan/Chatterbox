package android.sabertechnologies.com.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ImgActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    TextView tv;

    @Override
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
        setContentView(R.layout.activity_img);

        progressBar = (ProgressBar) findViewById(R.id.pbimgshow);
        tv = (TextView) findViewById(R.id.lt);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        progressBar.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);

        DatabaseReference d = FirebaseDatabase.getInstance().getReference();

        imageView = (ImageView) findViewById(R.id.imgholder);

        d.child("Images").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.getKey().equals(getIntent().getStringExtra("St"))){
                    Picasso.with(getApplicationContext()).load(String.valueOf(dataSnapshot.getValue(String.class))).into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.INVISIBLE);
                                tv.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext() , ChatRoom.class);
        i.putExtra("Key" , getIntent().getStringExtra("ke"));
        startActivity(i);
        finish();
    }

}