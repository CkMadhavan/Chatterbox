package android.sabertechnologies.com.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by madhav on 3/30/19.
 */

public class DBForBot extends SQLiteOpenHelper {

    public static final String col1 = "ID";
    public static final String col2 = "Messages";
    public static final String dbName = "SaberDB.db";
    public static final String table = "MessagesTable";

    public DBForBot(Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MessagesTable (ID INTEGER PRIMARY KEY AUTOINCREMENT , Messages TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS MessagesTable");
        onCreate(db);
    }

    public boolean addTask(String keys) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(col2, keys);
        if (db.insert(table, null, cv) == -1) {
            return false;
        }
        return true;
    }

    public Cursor viewResults() {
        return getWritableDatabase().rawQuery("select * from MessagesTable", null);
    }
}
