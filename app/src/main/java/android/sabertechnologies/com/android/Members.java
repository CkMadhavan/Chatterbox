package android.sabertechnologies.com.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Collections;

public class Members extends AppCompatActivity {
    ArrayAdapter<String> a;
    String key;
    ListView lv,lv1;
    int pos;
    RelativeLayout rel;
    ArrayList<String> mem;
    ArrayList<String> k;
    SharedPreferences sharedPreferences;
    ArrayList<String> ai;
    boolean admin;
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
        setContentView(R.layout.activity_members);
        this.key = getIntent().getStringExtra("k");
        admin = false;
        ai = new ArrayList<String>();
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        pos = 0;
        ia = new Ada(getApplicationContext() , ai);
        this.mem = new ArrayList();
        this.k = new ArrayList();
        this.lv = (ListView) findViewById(R.id.list);
        this.lv1 = (ListView) findViewById(R.id.lv1);
        rel = (RelativeLayout) findViewById(R.id.r);
        rel.setVisibility(View.INVISIBLE);
        sharedPreferences = getSharedPreferences("Pref" , MODE_PRIVATE);
        this.a = new ArrayAdapter(getApplicationContext(), R.layout.tvforlv, this.mem);
        this.lv.setAdapter(this.a);

        lv1.setAdapter(ia);

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = ai.get(i);
                Intent inte = new Intent(getApplicationContext(),ImgGCM2.class);
                inte.putExtra("gpi" , s);
                inte.putExtra("ke" , mem.get(i));
                inte.putExtra("key" , key);
                startActivity(inte);
                finish();
            }
        });

        FirebaseDatabase.getInstance().getReference().child(this.key).child("Members").addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Members.this.mem.add(dataSnapshot.getValue(String.class));
                k.add(dataSnapshot.getKey());
                Members.this.a.notifyDataSetChanged();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try{
                    mem.remove(mem.get(pos));
                    k.remove(k.get(pos));
                    a.notifyDataSetChanged();
                }
                catch(Exception e){
                    //Do Nothing
                }
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });
        FirebaseDatabase.getInstance().getReference().child("UsrImgs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String url = dataSnapshot.getValue(String.class);
                String usr = dataSnapshot.getKey().toString();
                int num1 = 0;

                for(int i = 0 ; i < usr.length() ; i++){
                    if(Character.isLetter(usr.charAt(i))){
                        num1 = i;
                        break;
                    }
                }

                if(mem.contains(usr.substring(num1))){
                    ai.add(url);
                    ia.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try{
                    ai.remove(ai.get(pos));
                    ia.notifyDataSetChanged();
                }
                catch(Exception e){
                    //Do Nothing
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int inte, long l) {
                if(mem.contains(sharedPreferences.getString("Key", "None"))) {
                    if (Members.this.mem.indexOf(sharedPreferences.getString("Key", "None")) == 0) {
                        admin = true;
                }
                }
                if(admin){
                    if(inte != 0) {
                        rel.setVisibility(View.VISIBLE);
                        pos = inte;
                    }
                }else{
                    rel.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext() , "You Cannot Remove An User As You Are Not An Admin" ,Toast.LENGTH_LONG).show();
                }
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
    }

    public void yes(View v){
        FirebaseDatabase.getInstance().getReference().child(key).child("Members").child(k.get(pos)).removeValue();
        Toast.makeText(getApplicationContext(), "User Has Been Removed", Toast.LENGTH_LONG).show();
        rel.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(getApplicationContext() , ChatRoom.class);
        intent.putExtra("Key" , key);
        startActivity(intent);
        finish();
    }

    public void no(View v){
        rel.setVisibility(View.INVISIBLE);
    }

    public void add(View v){
        if(mem.contains(sharedPreferences.getString("Key", "None"))) {
            Intent i = new Intent(getApplicationContext(), Add.class);
            i.putExtra("k", this.key);
            i.putExtra("m", this.mem);
            startActivity(i);
            finish();
        }else {
            Toast.makeText(getApplicationContext() , "You Cannot Add Users As You Have Been Removed From This Group",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext() , ChatRoom.class);
        i.putExtra("Key" , this.key);
        startActivity(i);
        finish();
    }

}
