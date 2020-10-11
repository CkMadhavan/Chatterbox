package android.sabertechnologies.com.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by madhav on 6/15/19.
 */

public class Ada extends BaseAdapter {

    private Context context;
    DatabaseReference db;
    int a;
    ArrayList<String> ai;

    public Ada(Context context , ArrayList<String> ai) {
        this.db = FirebaseDatabase.getInstance().getReference();
        this.context = context;
        this.ai = ai;
    }

    @Override
    public int getCount() {

        return ai.size();
    }

    @Override
    public Object getItem(int i) {

        return ai.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = View.inflate(context , R.layout.lvimg , null);

        ImageView images = (ImageView) view.findViewById(R.id.img);
        Picasso.with(context).load(ai.get(i)).memoryPolicy(MemoryPolicy.NO_CACHE).into(images);

        return view;
    }
}
