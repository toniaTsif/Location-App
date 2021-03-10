package gr.hua.android.locationapp;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class LocationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(),"Service started!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"Service stopped!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ContentResolver resolver = this.getContentResolver();
        //Get data from intent
        ArrayList<LocationContract> contractArrayList = (ArrayList<LocationContract>) intent.getSerializableExtra("array");

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                if(contractArrayList.isEmpty()){
                    Toast.makeText(LocationService.this.getApplicationContext(), "List is empty.", Toast.LENGTH_SHORT).show();
                }else {
                    //Get all rows of table
                    Cursor cursor = resolver.query(Uri.parse(LocationContentProvider.CONTENT_URI+"/locations"),null,null,null,null);
                    ArrayList<Integer> cursorData = new ArrayList<Integer>();
                    if (cursor.moveToFirst()){
                        //For each row, add id into cursorData array
                        while(!cursor.isAfterLast()){
                            int id = cursor.getInt(cursor.getColumnIndex("id"));
                            cursorData.add(id);
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                    //Get array's size
                    int lastId = cursorData.size();

                    //Save locations to DB through the content provider
                    for(LocationContract contract: contractArrayList){
                        ContentValues values = new ContentValues();
                        //Put as id the number of rows saved in the table
                        values.put("id",lastId);
                        values.put("latitude",contract.getLatitude());
                        values.put("longitude",contract.getLongitude());
                        values.put("timestamp",contract.getTimestamp());
                        try {
                            Uri result = resolver.insert(Uri.parse(LocationContentProvider.CONTENT_URI + "/locations"), values);
                        } catch(NullPointerException e){}
                        lastId++;
                    }
                }
                stopSelf();
            }
        });
        thread.start();
        return Service.START_NOT_STICKY;
    }
}
