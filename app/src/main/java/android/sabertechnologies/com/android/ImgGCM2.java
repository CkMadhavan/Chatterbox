package android.sabertechnologies.com.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class ImgGCM2 extends AppCompatActivity {

    ImageView iv;
    FirebaseStorage fs = FirebaseStorage.getInstance();
    String k,key;
    ProgressBar progressBar;
    TextView tv2;

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
        setContentView(R.layout.activity_img_gcm2);
        Intent i = getIntent();
        iv = (ImageView) findViewById(R.id.ii);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        String s = i.getStringExtra("gpi");
        k = i.getStringExtra("ke");
        key = i.getStringExtra("key");
        Picasso.with(getApplicationContext()).load(s).into(iv);
        progressBar = (ProgressBar) findViewById(R.id.pbimgshow);
        tv2 = (TextView) findViewById(R.id.lt);
        progressBar.setVisibility(View.INVISIBLE);
        tv2.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext() , Members.class);
        i.putExtra("k" , key);
        startActivity(i);
        finish();
    }
}
