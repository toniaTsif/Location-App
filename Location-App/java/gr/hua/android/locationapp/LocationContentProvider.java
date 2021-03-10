package gr.hua.android.locationapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LocationContentProvider extends ContentProvider {
    private static UriMatcher uriMatcher;
    private static final String AUTHORITY= "gr.hua.android.locationapp";
    public static final String CONTENT_URI ="content://"+AUTHORITY;
    private DBHelper dbhelper;
    static{
        uriMatcher= new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,"locations",1);
        uriMatcher.addURI(AUTHORITY,"locations/#",2);
    }

    @Override
    public boolean onCreate() {
        dbhelper = new DBHelper(getContext());
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor result = null;
        switch (uriMatcher.match(uri)){
            case 1:
                result = dbhelper.selectAll();
                break;
        }
        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri result = null;
        switch (uriMatcher.match(uri)){
            case 1:
                //Get contract data from values
                LocationContract contract= new LocationContract(values.getAsInteger("id"),values.getAsFloat("latitude"),values.getAsFloat("longitude"),values.getAsInteger("timestamp"));
                //Save a location record to DB
                long id = dbhelper.insert(contract);
                result = Uri.parse(AUTHORITY+"/locations/"+id);
                break;
        }
        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
