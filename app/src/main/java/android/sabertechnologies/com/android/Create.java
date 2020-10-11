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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.UUID;

public class Create extends AppCompatActivity {
    char character1;
    DatabaseReference db;
    DB database;
    EditText etSubject;
    EditText etTitle;
    DatabaseReference p;
    FirebaseStorage fs = FirebaseStorage.getInstance();
    int rand1;
    int rand2;
    ImageView iv;
    SharedPreferences s;
    TextView tvKey;
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
        this.s = getSharedPreferences("Pref", 0);
        setContentView(R.layout.activity_create);
        this.etTitle = (EditText) findViewById(R.id.etTitle);
        this.database = new DB(getApplicationContext());
        iv = (ImageView) findViewById(R.id.gl);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        this.etSubject = (EditText) findViewById(R.id.etSubject);
        this.tvKey = (TextView) findViewById(R.id.tvKey);
        this.db = FirebaseDatabase.getInstance().getReference();
        Random r = new Random();
        this.rand1 = r.nextInt(89999) + 100000;
        this.rand2 = r.nextInt(9);
        this.character1 = (char) (r.nextInt(25) + 97);
        db.child("NumberOfGroups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nou = Integer.parseInt(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        this.etTitle.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Create.this.tvKey.setText(charSequence + String.valueOf(Create.this.rand2) + Create.this.character1 + String.valueOf(Create.this.rand1));
            }

            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void create(View v) {
        if(!etTitle.getText().toString().equals("")) {
            if(!etSubject.getText().toString().equals("")) {

                Bitmap bitm = ((BitmapDrawable)iv.getDrawable()).getBitmap();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitm.compress(Bitmap.CompressFormat.PNG , 100 , baos);
                byte[] d = baos.toByteArray();

                String m = String.valueOf(UUID.randomUUID());


                String path = "groups" + "/" + tvKey.getText().toString() + ".png";
                StorageReference sto = fs.getReference(path);

                StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("meta" , etTitle.getText().toString()).build();

                UploadTask upt = sto.putBytes(d , metadata);

                upt.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        db.child("NumberOfGroups").setValue(String.valueOf(nou + 1));
                        String url = taskSnapshot.getDownloadUrl().toString();
                        db.child("GrpImgs").child(String.valueOf(nou + 1) + '_' +tvKey.getText().toString()).setValue(url);
                    }
                });

                this.p = this.db.child(this.tvKey.getText().toString());
                this.p.child("Title").setValue(this.etTitle.getText().toString());
                this.p.child("Topic").setValue(this.etSubject.getText().toString());
                SharedPreferences sh = getSharedPreferences(this.tvKey.getText().toString(), 0);
                String k = this.p.child("Members").push().getKey();
                Editor ed = sh.edit();
                ed.putString(this.tvKey.getText().toString(), k);
                ed.commit();
                this.p.child("Members").child(k).setValue(this.s.getString("Key", "NONE"));
                Intent i1 = new Intent(getApplicationContext(), ChatRoom.class);
                this.database.addTask(this.tvKey.getText().toString());
                i1.putExtra("Key", this.tvKey.getText().toString());
                startActivity(i1);
                finish();
            }else {
                Toast.makeText(getApplicationContext() , "Do Not Let Any Fields Empty" , Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext() , "Do Not Let Any Fields Empty" , Toast.LENGTH_LONG).show();
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
        Intent i = new Intent(getApplicationContext() , MyGroups.class);
        startActivity(i);
        finish();
    }

}
