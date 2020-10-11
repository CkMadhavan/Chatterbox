package android.sabertechnologies.com.android;

import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.BuildConfig;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class Saberbot extends AppCompatActivity {

    ArrayAdapter<SpannableString> a;
    EditText et;
    ListView lv;
    SharedPreferences sha;
    ActionBar action;
    String string;
    DBForBot d;
    int h , mo , month ,date , year , aop;
    ProgressBar pb;
    String am;
    RequestQueue mQueue, mQueue1;
    ArrayList<SpannableString> m;
    boolean del;
    ClipboardManager cm;
    String url,url2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sh = getSharedPreferences("themePref" , MODE_PRIVATE);
        setTheme(R.style.gra);
        d = new DBForBot(getApplicationContext());
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

        setContentView(R.layout.activity_saberbot);
        action = getSupportActionBar();
        sha = getSharedPreferences("Pref", 0);
        this.lv = (ListView) findViewById(R.id.lv);
        m = new ArrayList<SpannableString>();
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        this.a = new ArrayAdapter<SpannableString>(getApplicationContext(), R.layout.tvforlv1, m);
        this.lv.setAdapter(this.a);
        pb = (ProgressBar) findViewById(R.id.pbimgshow);
        pb.setVisibility(View.INVISIBLE);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mQueue1 = Volley.newRequestQueue(getApplicationContext());
        del = false;
        this.et = (EditText) findViewById(R.id.editText);
        cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        lv.setLongClickable(true);

        FirebaseDatabase.getInstance().getReference().child("SaberbotURL").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                url = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("SaberbotURL2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                url2 = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Cursor show = d.viewResults();
        if (show.getCount() == 0) {
            am = "PM";
            Calendar c = Calendar.getInstance();
            h = c.get(Calendar.HOUR);
            mo = c.get(Calendar.MINUTE);
            aop = c.get(Calendar.AM_PM);
            date = c.get(Calendar.DATE);
            month = c.get(Calendar.MONTH) + 1;
            year = c.get(Calendar.YEAR);
            if (aop == 0) {
                am = "AM";
            } else if (aop == 1) {
                am = "PM";
            }

            String time = "- " + String.valueOf(h) + ":" + String.valueOf(mo) + " " + am;
            String final_text = "Saberbot" + " :" + "\n" + "     " + "Hi Chatterbox User" + "     " + time + " - " + date + "/" + month + "/" + year ;

            int indexOfColon1 = final_text.indexOf(":") + 1;
            int indexOfHyphen1 = final_text.lastIndexOf("-" , final_text.lastIndexOf("-")-1);

            SpannableString spannableString2 = new SpannableString(final_text);
            spannableString2.setSpan(new ForegroundColorSpan(Color.RED), 0, indexOfColon1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString2.setSpan(new RelativeSizeSpan(0.75f), 0, indexOfColon1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString2.setSpan(new ForegroundColorSpan(Color.BLUE), indexOfHyphen1-1, final_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString2.setSpan(new RelativeSizeSpan(0.5f), indexOfHyphen1-1, final_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            m.add(spannableString2);
            a.notifyDataSetChanged();
            d.addTask(final_text);

            String final1 = "Saberbot" + " :" + "\n" + "     " + "Please Remember That I Am Just A Simple Chatbot And I Do Not Know A Lot About The World . I Do Not Understand Context . Also Note That I Might Be A Bit Slow In Replying . I Know That's A Lot Of Barriers But I Don't Think That Should Stop Us From Having A Nice Little Conversation . So Please Bare With Me ." + "     " + time + " - " + date + "/" + month + "/" + year ;

            int indexOfColon3 = final1.indexOf(":") + 1;
            int indexOfHyphen3 = final1.lastIndexOf("-" , final1.lastIndexOf("-")-1);

            SpannableString spannableString3 = new SpannableString(final1);
            spannableString3.setSpan(new ForegroundColorSpan(Color.RED), 0, indexOfColon3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString3.setSpan(new RelativeSizeSpan(0.75f), 0, indexOfColon3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString3.setSpan(new ForegroundColorSpan(Color.BLUE), indexOfHyphen3-1, final1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString3.setSpan(new RelativeSizeSpan(0.5f), indexOfHyphen3-1, final1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            m.add(spannableString3);
            a.notifyDataSetChanged();
            d.addTask(final1);
        }
        while (show.moveToNext()) {

            int indexOfColon = show.getString(1).indexOf(":") + 1;
            int indexOfHyphen = show.getString(1).lastIndexOf("-" , show.getString(1).lastIndexOf("-")-1);

            SpannableString spannableString = new SpannableString(show.getString(1));
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, indexOfColon, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(0.75f), 0, indexOfColon, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), indexOfHyphen-1, show.getString(1).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(0.5f), indexOfHyphen-1, show.getString(1).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            m.add(spannableString);
            a.notifyDataSetChanged();
        }

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

    public void send(View v){

        String text = this.et.getText().toString();
        if (!text.equals("")) {
            am = "PM";
            Calendar c = Calendar.getInstance();
            h = c.get(Calendar.HOUR);
            mo = c.get(Calendar.MINUTE);
            aop = c.get(Calendar.AM_PM);
            date = c.get(Calendar.DATE);
            month = c.get(Calendar.MONTH) + 1;
            year = c.get(Calendar.YEAR);
            if (aop == 0) {
                am = "AM";
            } else if (aop == 1) {
                am = "PM";
            }

            String time = "- " + String.valueOf(h) + ":" + String.valueOf(mo) + " " + am;
            String final_text = this.sha.getString("Key", "None") + " :" + "\n" + "     " + text + "     " + time + " - " + date + "/" + month + "/" + year ;

            int indexOfColon4 = final_text.indexOf(":") + 1;
            int indexOfHyphen4 = final_text.lastIndexOf("-" , final_text.lastIndexOf("-")-1);

            SpannableString spannableString4 = new SpannableString(final_text);
            spannableString4.setSpan(new ForegroundColorSpan(Color.RED), 0, indexOfColon4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString4.setSpan(new RelativeSizeSpan(0.75f), 0, indexOfColon4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString4.setSpan(new ForegroundColorSpan(Color.BLUE), indexOfHyphen4-1, final_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString4.setSpan(new RelativeSizeSpan(0.5f), indexOfHyphen4-1, final_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            m.add(spannableString4);
            a.notifyDataSetChanged();

            d.addTask(final_text);

            this.et.setText("");

            pb.setVisibility(View.VISIBLE);

            new getData().execute(url + "/" + text.replace(' ' , '_'));


            StringRequest request = new StringRequest(Request.Method.GET, "https://sentimentsaber.herokuapp.com/" + text.replace(' ' , '_'),
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

                            //Intent inten = new Intent(getApplicationContext() , Saberbot.class);
                            //startActivity(inten);
                            //finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            mQueue.add(request);

        }

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext() , MyGroups.class);
        startActivity(i);
        finish();
    }

    class getData extends AsyncTask<String,String,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                URL url = new URL(strings[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String val = in.readLine().toString().trim();



                String time = "- " + String.valueOf(h) + ":" + String.valueOf(mo) + " " + am;
                String final_text = "Saberbot" + " :" + "\n" + "     " + val + "     " + time + " - " + date + "/" + month + "/" + year ;

                int indexOfColon5 = final_text.indexOf(":") + 1;
                int indexOfHyphen5 = final_text.lastIndexOf("-" , final_text.lastIndexOf("-")-1);

                SpannableString spannableString5 = new SpannableString(final_text);
                spannableString5.setSpan(new ForegroundColorSpan(Color.RED), 0, indexOfColon5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString5.setSpan(new RelativeSizeSpan(0.75f), 0, indexOfColon5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString5.setSpan(new ForegroundColorSpan(Color.BLUE), indexOfHyphen5-1, final_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString5.setSpan(new RelativeSizeSpan(0.5f), indexOfHyphen5-1, final_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                m.add(spannableString5);
                //a.notifyDataSetChanged();
                d.addTask(final_text);

                Intent inten = new Intent(Saberbot.this , Saberbot.class);
                startActivity(inten);
                finish();

            } catch (MalformedURLException e) {
                System.out.println("[ERROR] : Mal");
            } catch (IOException e) {
                System.out.println("[ERROR] : IO");
                new getData().execute(strings[0].replace(url , url2));
            }
            return null;
        }
    }

}


