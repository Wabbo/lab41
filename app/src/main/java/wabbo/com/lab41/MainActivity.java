package wabbo.com.lab41;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView longt, attitd;
    Button getLoc, getStreat, SMS;
    String address , sms ,phone="999999999999" ;


    int PREMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        longt = findViewById(R.id.longt);
        attitd = findViewById(R.id.attitd);
        getLoc = findViewById(R.id.getLoc);
        getStreat = findViewById(R.id.getStreat);
        SMS = findViewById(R.id.sms);

        getLoc.setOnClickListener(v -> {
                getlastLocation();
        });

        getStreat.setOnClickListener(v -> {

            Toast.makeText(this, address, Toast.LENGTH_SHORT).show();
        });
        SMS.setOnClickListener(v -> {
            sendSMS();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (chackPermissions()) {
            getlastLocation();
        }
    }

    private void getlastLocation() {
        if (chackPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData();
            } else {
                Toast.makeText(this, "Turn on Permissions", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean chackPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PREMISSION_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PREMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getlastLocation();
            }
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(0);
        //request.setFastestInterval(0);
        request.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(request, callback, Looper.myLooper());
    }

    private LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            longt.setText(String.valueOf(location.getLongitude()));
            attitd.setText(String.valueOf(location.getLatitude()));
             getStreatName(location);


        }
    };

    private void getStreatName(Location location) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String addres = addressList.get(0).getAddressLine(0);
            address = addres ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendSMS(){
        Intent  intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+phone) ) ;
        intent.putExtra("sms_body",longt.getText().toString()+" "+attitd.getText().toString()) ;
        startActivity(intent);
    }
}
