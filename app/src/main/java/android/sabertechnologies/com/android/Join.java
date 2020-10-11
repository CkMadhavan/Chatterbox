package android.sabertechnologies.com.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Join extends AppCompatActivity {
    ArrayList<String> a;
    DB database;
    EditText et;
    DatabaseReference k;
    ArrayList<String> keys;

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
        setContentView(R.layout.activity_join);
        this.et = (EditText) findViewById(R.id.etEnterKey);
        this.keys = new ArrayList();
        this.database = new DB(getApplicationContext());
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        this.a = new ArrayList();
        this.k = FirebaseDatabase.getInstance().getReference();
        this.k.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Join.this.keys.add(dataSnapshot.getKey());
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Cursor show = this.database.viewResults();
        while (show.moveToNext()) {
            this.a.add(show.getString(1));
        }
    }

    public void join(View v) {
        Intent i;
        if (!this.keys.contains(this.et.getText().toString())) {
            i = new Intent(getApplicationContext(), KeyNotAvailable.class);
            i.putExtra("P", "key");
            startActivity(i);
            finish();
        } else if (this.a.contains(this.et.getText().toString())) {
            i = new Intent(getApplicationContext(), KeyNotAvailable.class);
            i.putExtra("P", "Alr");
            startActivity(i);
            finish();
        } else {
            Intent i1 = new Intent(getApplicationContext(), ChatRoom.class);
            this.database.addTask(this.et.getText().toString());
            SharedPreferences s = getSharedPreferences("Pref", 0);
            SharedPreferences sh = getSharedPreferences(this.et.getText().toString(), 0);
            String b = this.k.child(this.et.getText().toString()).child("Members").push().getKey();
            Editor ed = sh.edit();
            ed.putString(this.et.getText().toString(), b);
            ed.commit();
            this.k.child(this.et.getText().toString()).child("Members").child(b).setValue(s.getString("Key", "NONE"));
            i1.putExtra("Key", this.et.getText().toString());
            startActivity(i1);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext() , MyGroups.class);
        startActivity(i);
        finish();
    }

}
