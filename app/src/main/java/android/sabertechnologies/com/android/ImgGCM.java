package android.sabertechnologies.com.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ImgGCM extends AppCompatActivity {

    ImageView iv;
    FirebaseStorage fs = FirebaseStorage.getInstance();
    String k;
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
        setContentView(R.layout.activity_img_gcm);
        Intent i = getIntent();
        iv = (ImageView) findViewById(R.id.ii);
        String s = i.getStringExtra("gpi");
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        k = i.getStringExtra("ke");
        Picasso.with(getApplicationContext()).load(s).into(iv);
        progressBar = (ProgressBar) findViewById(R.id.pbimgshow);
        tv2 = (TextView) findViewById(R.id.lt);
        progressBar.setVisibility(View.INVISIBLE);
        tv2.setVisibility(View.INVISIBLE);
    }

    public void selImage2(View v){
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


                String path = "groups/"+ k + ".png";
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
                        Toast.makeText(getApplicationContext() , "Successfully Changed the Group Image" , Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        tv2.setVisibility(View.INVISIBLE);
                        Intent inte = new Intent(getApplicationContext(),MyGroups.class);
                        startActivity(inte);
                        finish();
                    }
                });

            }catch (Exception e){
                Toast.makeText(getApplicationContext() , "No Image Selected" , Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext() , MyGroups.class);
        startActivity(i);
        finish();
    }

}
