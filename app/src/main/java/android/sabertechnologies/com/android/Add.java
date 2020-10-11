package android.sabertechnologies.com.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Add extends AppCompatActivity {

    EditText et;
    String k;
    ArrayList<String> a , members;

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

        setContentView(R.layout.activity_add);
        Intent in = getIntent();
        et = (EditText) findViewById(R.id.editText2);
        a = new ArrayList<String>();
        members = new ArrayList<String>();
        k = in.getStringExtra("k");
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        members = in.getStringArrayListExtra("m");
        FirebaseDatabase.getInstance().getReference().child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                a.add(dataSnapshot.getValue(String.class));
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

    public void addusr(View v){
        if(a.contains(et.getText().toString())) {
            if (!members.contains(et.getText().toString())) {
                String pk = FirebaseDatabase.getInstance().getReference().child(k).child("Members").push().getKey();
                FirebaseDatabase.getInstance().getReference().child(k).child("Members").child(pk).setValue(et.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("AddRequests").child(pk).setValue(et.getText().toString() + "-" +k);
                Toast.makeText(getApplicationContext() , "Successfully Added the User" , Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext() , ChatRoom.class);
                i.putExtra("Key" , k);
                startActivity(i);
                finish();
            }else{
                Toast.makeText(getApplicationContext() , "Specified User Is Already In The Group" , Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext() , "No Such Username Exists" , Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext() , Members.class);
        i.putExtra("k" , this.k);
        startActivity(i);
        finish();
    }
}
