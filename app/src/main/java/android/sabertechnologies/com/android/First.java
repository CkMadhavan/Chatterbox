package android.sabertechnologies.com.android;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.UUID;

public class First extends AppCompatActivity {
    EditText et;
    DatabaseReference d , p;
    ArrayList<String> a;
    ImageView iv;
    FirebaseStorage fs = FirebaseStorage.getInstance();
    int nou;

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
        setContentView(R.layout.activity_first);
        this.et = (EditText) findViewById(R.id.e);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        d = FirebaseDatabase.getInstance().getReference();
        p = d.child("Users");
        iv = (ImageView) findViewById(R.id.gl);
        a = new ArrayList<String>();

        d.child("NumberOfUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nou = Integer.parseInt(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        p.addChildEventListener(new ChildEventListener() {
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

    public void con(View v) {
        if (this.et.getText().toString() != BuildConfig.FLAVOR && this.et.getText().toString() != "None") {
            if(!et.getText().toString().equals("")) {
                if(!et.getText().toString().contains("-")) {
                    if(!et.getText().toString().contains(":")) {
                        if (!et.getText().toString().contains(".") || !et.getText().toString().contains("$") || !et.getText().toString().contains("#") || !et.getText().toString().contains("[") || !et.getText().toString().contains("]")) {
                            if(Character.isLetter(et.getText().toString().charAt(0))) {
                                if (!a.contains(et.getText().toString())) {

                                    Bitmap bitm = ((BitmapDrawable) iv.getDrawable()).getBitmap();

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                    byte[] di = baos.toByteArray();

                                    String m = String.valueOf(UUID.randomUUID());

                                    String path = "usrs" + "/" + et.getText().toString() + ".png";
                                    StorageReference sto = fs.getReference(path);

                                    StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("meta", et.getText().toString()).build();

                                    UploadTask upt = sto.putBytes(di, metadata);


                                    upt.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            d.child("NumberOfUsers").setValue(String.valueOf(nou + 1));
                                            String url = taskSnapshot.getDownloadUrl().toString();
                                            d.child("UsrImgs").child(String.valueOf(nou + 1) + et.getText().toString()).setValue(url);
                                        }
                                    });

                                    Editor e = getSharedPreferences("Pref", 0).edit();
                                    e.putString("Key", this.et.getText().toString());
                                    e.commit();
                                    p.push().setValue(et.getText().toString());
                                    startActivity(new Intent(getApplicationContext(), MyGroups.class));
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Username Already Exists , Please Select A Different One", Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Toast.makeText(getApplicationContext(), "Username Can Start With Alphabets Only", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Do Not Use The Characters '.' , '#' , '$' , '[' , ']' , In Your Username", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Do Not Use The Character ':' ,  In Your Username", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext() , "Do Not Use The Character '-' In Your Username" , Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext() , "Username Cannot Be Empty" , Toast.LENGTH_LONG).show();
            }
        }
    }

    public void selImage1(View v){
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
                iv.setImageBitmap(bitm);

            }catch (Exception e){
                Toast.makeText(getApplicationContext() , "No Image Selected" , Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
