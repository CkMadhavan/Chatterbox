package android.sabertechnologies.com.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    public static final String COL_1 = "ID";
    public static final String COL_2 = "keys";
    public static final String DATABASE_NAME = "Mg.db";
    public static final String TABLE_NAME = "MYGROUPS";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MYGROUPS (ID INTEGER PRIMARY KEY AUTOINCREMENT , keys TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS MYGROUPS");
        onCreate(db);
    }

    public boolean addTask(String keys) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_2, keys);
        if (db.insert(TABLE_NAME, null, cv) == -1) {
            return false;
        }
        return true;
    }

    public boolean deleteTask(String Title) {
        if (((long) getWritableDatabase().delete(TABLE_NAME, "keys = ?", new String[]{Title})) == -1) {
            return false;
        }
        return true;
    }

    public Cursor viewResults() {
        return getWritableDatabase().rawQuery("select * from MYGROUPS", null);
    }
}
