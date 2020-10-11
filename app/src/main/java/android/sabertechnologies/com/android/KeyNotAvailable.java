package android.sabertechnologies.com.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class KeyNotAvailable extends AppCompatActivity {
    TextView t;

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

        setContentView(R.layout.activity_key_not_available);
        Intent g = getIntent();
        this.t = (TextView) findViewById(R.id.textView);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        if (g.getStringExtra("P") == "key") {
            this.t.setText("The Key You Mentioned Does Not Exist");
        } else {
            this.t.setText("The Key You Mentioned Does Not Exist (Or) You Are Already In This Group . You Will Be Able To Access The Group By Going To 'My Chat Groups'");
        }
    }

    public void goBack(View v) {
        startActivity(new Intent(getApplicationContext(), MyGroups.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext() , Join.class);
        startActivity(i);
        finish();
    }
}
