package android.sabertechnologies.com.android;

import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class ChatRoom extends AppCompatActivity {
    ArrayAdapter<SpannableString> a;
    DatabaseReference d;
    DB db;
    EditText et;
    String k;
    ListView lv;
    ArrayList<SpannableString> m;
    ArrayList<String> usernames;
    String p;
    SharedPreferences sha;
    String title;
    String topic;
    ActionBar action;
    TextView tv;
    TextView tv1;
    String string;
    FirebaseStorage fs = FirebaseStorage.getInstance();
    RequestQueue mQueue;
    TextView rmv;
    boolean del;
    String strings;
    String msg;
    ClipboardManager cm;
    ProgressBar progressBar;
    TextView tv2;

    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sh = getSharedPreferences("themePref" , MODE_PRIVATE);
        setTheme(R.style.gra);
        String str = sh.getString("th" , "None");
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

        Intent i = getIntent();
        setContentView(R.layout.activity_chat_room);
        this.db = new DB(getApplicationContext());
        action = getSupportActionBar();
        this.d = FirebaseDatabase.getInstance().getReference();
        this.m = new ArrayList<SpannableString>();
        this.usernames = new ArrayList<String>();
        this.sha = getSharedPreferences("Pref", 0);
        this.lv = (ListView) findViewById(R.id.lv);
        this.a = new ArrayAdapter<SpannableString>(getApplicationContext(), R.layout.tvforlv1, this.m);
        this.lv.setAdapter(this.a);
        this.tv = (TextView) findViewById(R.id.t);
        progressBar = (ProgressBar) findViewById(R.id.pbimgshow);
        tv2 = (TextView) findViewById(R.id.lt);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        progressBar.setVisibility(View.INVISIBLE);
        tv2.setVisibility(View.INVISIBLE);
        rmv = (TextView) findViewById(R.id.rtv);
        rmv.setVisibility(View.INVISIBLE);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        del = false;
        this.tv1 = (TextView) findViewById(R.id.t2);
        this.et = (EditText) findViewById(R.id.editText);
        cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        lv.setLongClickable(true);
        k = i.getStringExtra("Key");

        d.child("Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strings = String.valueOf(dataSnapshot.getValue(String.class));
                string = "Image" + strings;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child(this.k).child("Members").addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                usernames.add(dataSnapshot.getValue(String.class));
                if (usernames.contains(sha.getString("Key", "None"))) {
                    del = true;
                    lv.setVisibility(View.VISIBLE);
                    rmv.setVisibility(View.INVISIBLE);
                }else {
                    del = false;
                    lv.setVisibility(View.INVISIBLE);
                    rmv.setVisibility(View.VISIBLE);
                }
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
        this.d.child(this.k).child("Messages").addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String c) {
                String str = dataSnapshot.getValue(String.class);

                int indexOfColon = str.indexOf(":") + 1;
                int indexOfHyphen = str.lastIndexOf("-" , str.lastIndexOf("-")-1);
                if(!str.substring(0,indexOfColon).contains("-- IMAGE (Tap To View)")){
                    String tbr = str.substring(indexOfColon , indexOfHyphen).trim();

                    String dec = "";
                    int decShift = (int)(tbr.charAt(tbr.length() -1));
                    for(int i = 0 ; i < tbr.length() -1 ; i++){
                        dec+=(char)((int)tbr.charAt(i) - decShift);
                    }

                    str = str.replace(tbr , dec);

                }

                SpannableString spannableString = new SpannableString(str);
                spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, indexOfColon, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new RelativeSizeSpan(0.75f), 0, indexOfColon, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), indexOfHyphen-1, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new RelativeSizeSpan(0.5f), indexOfHyphen-1, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ChatRoom.this.m.add(spannableString);
                ChatRoom.this.a.notifyDataSetChanged();
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
        this.d.child(this.k).child("Title").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatRoom.this.title = (String) dataSnapshot.getValue(String.class);
                ChatRoom.this.tv.setText(ChatRoom.this.title);
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });
        this.d.child(this.k).child("Topic").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatRoom.this.topic = (String) dataSnapshot.getValue(String.class);
                ChatRoom.this.tv1.setText("- " + ChatRoom.this.topic);
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String st = String.valueOf(lv.getItemAtPosition(i));
                try {
                    if (st.substring(st.indexOf('-') + 1, st.indexOf(':')).equals("- IMAGE (Tap To View) ")) {
                        int ioc = st.indexOf('\n') + 1;
                        String stri = st.substring(ioc);
                        int ioh = (stri.lastIndexOf("-", stri.lastIndexOf("-") - 1));
                        stri = stri.substring(0, ioh);
                        stri = stri.trim();
                        Intent inten = new Intent(getApplicationContext(), ImgActivity.class);
                        inten.putExtra("St", stri);
                        inten.putExtra("ke" , k);
                        startActivity(inten);
                        finish();
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext() , "Sorry That Was Not An Image" , Toast.LENGTH_LONG).show();
                }
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                String st = String.valueOf(lv.getItemAtPosition(i));
                int ioc = st.indexOf('\n') + 1;
                String stri = st.substring(ioc);
                int ioh = (stri.lastIndexOf("-", stri.lastIndexOf("-") - 1));
                stri = stri.substring(0, ioh);
                stri = stri.trim();

                if(!stri.equals("")){
                    ClipData cd = ClipData.newPlainText("text" , stri);
                    cm.setPrimaryClip(cd);

                    Toast.makeText(getApplicationContext() , "Message Copied To Clipboard" , Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext() , "No Text To Copy" , Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    public void send(View v) {
        if(del) {
            if (!et.getText().toString().equals("")) {
                String am = "PM";
                Calendar c = Calendar.getInstance();
                int h = c.get(Calendar.HOUR);
                int m = c.get(Calendar.MINUTE);
                int aop = c.get(Calendar.AM_PM);
                int date = c.get(Calendar.DATE);
                int month = c.get(Calendar.MONTH) + 1;
                int year = c.get(Calendar.YEAR);
                if (aop == 0) {
                    am = "AM";
                } else if (aop == 1) {
                    am = "PM";
                }
                String time = "- " + String.valueOf(h) + ":" + String.valueOf(m) + " " + am;
                msg = this.et.getText().toString();


                Random rand = new Random();
                int shift = rand.nextInt(1000000000);
                String txt = msg;
                String enc = "";
                String dec = "";


                for(int i = 0 ; i < txt.length() ; i++){
                    enc+=(char)((int)txt.charAt(i) + shift);
                }
                enc+=(char)shift;

                String str = this.sha.getString("Key", "None") + " :" + "\n" + "     " + enc + "     " + time + " - " + date + "/" + month + "/" + year;
                this.d.child(this.k).child("Messages").push().setValue(str);
                et.setText("");
                ArrayList<String> arrstr = new ArrayList<String>();
                arrstr = usernames;
                arrstr.remove(this.sha.getString("Key", "None"));
                String[] a = new String[arrstr.size()];
                a = arrstr.toArray(a);

                for(String x:a){
                    d.child("Unread").child(x).push().setValue(str);
                }

                StringRequest request = new StringRequest(Request.Method.GET, "https://sentimentsaber.herokuapp.com/" + msg.replace(' ' , '_'),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                String val = response.toString();

                                Float sentiment = Float.parseFloat(val);

                                SharedPreferences sh1 = getSharedPreferences("themePref" , MODE_PRIVATE);

                                if (sentiment > 0.80){

                                    SharedPreferences.Editor ed = sh1.edit();
                                    ed.putString("th" , "gr");
                                    ed.apply();

                                }else if(sentiment > 0.6){

                                    SharedPreferences.Editor ed = sh1.edit();
                                    ed.putString("th" , "bl");
                                    ed.apply();

                                }else if(sentiment > 0.4){

                                    SharedPreferences.Editor ed = sh1.edit();
                                    ed.putString("th" , "gra");
                                    ed.apply();

                                }else if(sentiment > 0.2){

                                    SharedPreferences.Editor ed = sh1.edit();
                                    ed.putString("th" , "ora");
                                    ed.apply();

                                }else if (sentiment >= 0){

                                    SharedPreferences.Editor ed = sh1.edit();
                                    ed.putString("th" , "red");
                                    ed.apply();

                                }

                                Intent inten = new Intent(getApplicationContext() , ChatRoom.class);
                                inten.putExtra("Key" , k);
                                startActivity(inten);
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                mQueue.add(request);

            } else {
                Toast.makeText(getApplicationContext(), "Cannot Send An Empty Message", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "You Cannot Post Any Messages To This Group As You Have Been Removed By The Group Admin", Toast.LENGTH_LONG).show();
        }
    }

    public void exitGroup(View v) {
        new Builder(this).setTitle("Exit ?").setMessage("Are You Sure That You Want To Exit From " + this.title).setPositiveButton("Yes", new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ChatRoom.this.db.deleteTask(ChatRoom.this.k);
                Toast.makeText(ChatRoom.this.getApplicationContext(), "You Have Exited From " + ChatRoom.this.title, Toast.LENGTH_LONG).show();
                String j = ChatRoom.this.getSharedPreferences(ChatRoom.this.k, 0).getString(ChatRoom.this.k, "NULL");
                ChatRoom.this.d.child(ChatRoom.this.k).child("Members").child(j).removeValue();
                ChatRoom.this.startActivity(new Intent(ChatRoom.this.getApplicationContext(), MyGroups.class));
                ChatRoom.this.finish();
            }
        }).setNegativeButton("No", null).show();
    }

    public void showMembers(View v) {
        Intent i = new Intent(getApplicationContext(), Members.class);
        i.putExtra("k", this.k);
        startActivity(i);
        finish();
    }

    public void selImage(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent,"Select Picture") , 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData() != null){

            Uri u = data.getData();
            try{

                Bitmap bitm = MediaStore.Images.Media.getBitmap(getContentResolver() , u);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitm.compress(Bitmap.CompressFormat.PNG , 100 , baos);
                byte[] d = baos.toByteArray();

                String m = String.valueOf(UUID.randomUUID());


                String path = k + "/" + m + ".png";
                StorageReference sto = fs.getReference(path);

                StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("meta" , m).build();

                UploadTask upt = sto.putBytes(d , metadata);

                progressBar.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);

                upt.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double percentage = (100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        tv2.setText(percentage+"% Uploaded");
                        progressBar.setProgress(((int) percentage));
                    }
                });

                upt.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri url = taskSnapshot.getDownloadUrl();
                        progressBar.setVisibility(View.INVISIBLE);
                        tv2.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext() , "Successfully Sent The Image" , Toast.LENGTH_LONG).show();
                        sendImg(url.toString());
                    }
                });

            }catch (Exception e){
                Toast.makeText(getApplicationContext() , "No Image Selected" , Toast.LENGTH_LONG).show();
            }

        }
    }

    public void sendImg(String Str) {

        if(del) {
            if (!Str.equals("")) {
                String am = "PM";
                Calendar c = Calendar.getInstance();
                int h = c.get(Calendar.HOUR);
                int m = c.get(Calendar.MINUTE);
                int aop = c.get(Calendar.AM_PM);
                int date = c.get(Calendar.DATE);
                int month = c.get(Calendar.MONTH) + 1;
                int year = c.get(Calendar.YEAR);
                if (aop == 0) {
                    am = "AM";
                } else if (aop == 1) {
                    am = "PM";
                }
                String time = "- " + String.valueOf(h) + ":" + String.valueOf(m) + " " + am;

                String msg = "Image" + strings;
                String str = this.sha.getString("Key", "None") + " -- IMAGE (Tap To View) :" + "\n" + "     " + msg + "     " + time + " - " + date + "/" + month + "/" + year;
                this.d.child(this.k).child("Messages").push().setValue(str);
                ArrayList<String> arrstr = new ArrayList<String>();
                arrstr = usernames;
                arrstr.remove(this.sha.getString("Key", "None"));
                String[] a = new String[arrstr.size()];
                a = arrstr.toArray(a);

                d.child("Images").child(string).setValue(Str);

                d.child("Image").setValue(String.valueOf(Integer.parseInt(strings) + 1));

                for(String x:a){
                    d.child("Unread").child(x).push().setValue(str);
                }

            } else {
                Toast.makeText(getApplicationContext(), "Cannot Send An Empty Message", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "You Cannot Post Any Messages To This Group As You Have Been Removed By The Group Admin", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext() , MyGroups.class);
        startActivity(i);
        finish();
    }

}