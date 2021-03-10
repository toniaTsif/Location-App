package gr.hua.android.locationapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static String DB_NAME="MyDB";
    public static String TABLE_NAME="LOCATIONS";
    public static String FIELD_1="id";
    public static String FIELD_2="longitude";
    public static String FIELD_3="latitude";
    public static String FIELD_4="timestamp";
    private String SQL_QUERY = "CREATE TABLE "+TABLE_NAME+" ("+FIELD_1+" INTEGER, "+FIELD_2+" FLOAT, "+FIELD_3+" FLOAT, "+FIELD_4+" INTEGER)";

    public DBHelper(@Nullable Context context) { super(context, DB_NAME, null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor selectAll(){
        return this.getReadableDatabase().query(TABLE_NAME,null,null,null,null,null,null);
    }

    public long insert(LocationContract contract){
        ContentValues values = new ContentValues();
        values.put(FIELD_1, contract.getId());
        values.put(FIELD_2,contract.getLongitude());
        values.put(FIELD_3,contract.getLatitude());
        values.put(FIELD_4,contract.getTimestamp());
        long id =this.getWritableDatabase().insert(TABLE_NAME,null,values);
        return id;
    }
}
