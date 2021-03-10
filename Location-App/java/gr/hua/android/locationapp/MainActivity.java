package gr.hua.android.locationapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.time.Instant;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    LocationListener locationListener;
    LocationManager manager;
    int id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), LocationService.class);
        ArrayList<LocationContract> contractArrayList = new ArrayList<>();

        //Button to start tracking user's location
        Button buttonStart= findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocation();
                Toast.makeText(MainActivity.this, "Tracking your location is starting.", Toast.LENGTH_SHORT).show();
            }
        });

        //Button to stop tracking user's location
        Button buttonStop=findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.removeUpdates(locationListener);
                Toast.makeText(MainActivity.this, "Tracking your location will soon stop.", Toast.LENGTH_SHORT).show();
                //Put array list with data into intent
                intent.putExtra("array",contractArrayList);
                //Start service to save all the locations to the database
                startService(intent);
            }
        });

        //When the power disconnects, show buttons
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "Power disconnected!", Toast.LENGTH_SHORT).show();
                buttonStart.setVisibility(View.VISIBLE);
                buttonStop.setVisibility(View.VISIBLE);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(receiver, filter);

        locationListener= new LocationListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationChanged(@NonNull Location location) {
                long time= Instant.now().getEpochSecond();
                Toast.makeText(MainActivity.this, "id:"+(id++)+ ", latitude:"+location.getLatitude()+", longitude:"+location.getLongitude()+", time:"+time, Toast.LENGTH_SHORT).show();
                LocationContract contract = new LocationContract((float)location.getLongitude(), (float)location.getLatitude(),time);
                contractArrayList.add(contract);
            }
        };
    }

    private void showLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return;
        }
        //Request updates between 5ms or 10m
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            for(int i=0;i<permissions.length;i++){
                if(Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])){
                    if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                        showLocation();
                    }
                }
            }
        }
    }
}