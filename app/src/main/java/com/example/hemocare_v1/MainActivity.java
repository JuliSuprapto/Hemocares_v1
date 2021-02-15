package com.example.hemocare_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.hemocare_v1.model.ModelAccess;
import com.example.hemocare_v1.utils.App;
import com.example.hemocare_v1.utils.GsonHelper;
import com.example.hemocare_v1.utils.Prefs;
import com.example.hemocare_v1.utils.Utils;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;
    boolean BackPress = false;
    ModelAccess profile;

    private DatabaseReference reference;
    private LocationManager manager;
    private final int MIN_TIME = 1000; //1sec
    private final int MAX_DISTANCE = 1; //1meter
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        profile = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        if (!Utils.isLoggedIn()) {
            bottomNav.setItemSelected(R.id.account, true);
            fragmentManager = getSupportFragmentManager();
            FragmentAccount accountFragment = new FragmentAccount();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, accountFragment).commit();
        } else {
            if (savedInstanceState == null) {
                bottomNav.setItemSelected(R.id.find, true);
                fragmentManager = getSupportFragmentManager();
                FragmentFind findFragment = new FragmentFind();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, findFragment).commit();
            }
            String _id_user = profile.get_id();
            manager = (LocationManager) getSystemService(LOCATION_SERVICE);
            reference = FirebaseDatabase.getInstance().getReference("location").child(_id_user);
        }

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch (id) {
                    case R.id.home:
                        fragment = new FragmentHome();
                        break;
                    case R.id.find:
                        fragment = new FragmentFind();
                        break;
                    case R.id.account:
                        fragment = new FragmentAccount();
                        break;
                }

                if (fragment != null) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                } else {
                    Log.e(TAG, "Error creating fragment");
                }
            }
        });

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        MapView maps = (MapView) findViewById(R.id.maps_google_driver);
        maps.getMapAsync(this);

        getLocationUpdate();
    }

    private void getLocationUpdate() {
        if (manager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MAX_DISTANCE, this);
                } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MAX_DISTANCE, this);
                } else {
                    StyleableToast.makeText(this, "Tidak ada provider...", R.style.toastStyleDefault).show();
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationUpdate();
            } else {
                StyleableToast.makeText(this, "Membutuhkan hak akses...", R.style.toastStyleDefault).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (BackPress) {
            super.onBackPressed();
            return;
        }
        this.BackPress = true;
        StyleableToast.makeText(this, "Tekan sekali lagi untuk keluar...", R.style.toastStyleDefault).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BackPress = false;
            }
        }, 2000);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            saveLocation(location);
        } else {
            StyleableToast.makeText(this, "Tidak ada lokasi...", R.style.toastStyleDefault).show();
        }
    }

    private void saveLocation(Location location) {
        if (Utils.isLoggedIn()) {
            reference.setValue(location);
            Log.d("DATA LOCATION", String.valueOf(location));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(31, 74);
    }
}
