package com.example.hemocare_v1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hemocare_v1.model.ModelAccess;
import com.example.hemocare_v1.server.BaseURL;
import com.example.hemocare_v1.utils.App;
import com.example.hemocare_v1.utils.GsonHelper;
import com.example.hemocare_v1.utils.Prefs;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FragmentFind extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MapView mapView;
    private GoogleMap googleMaps;
    private GoogleMapOptions googleMapOptions;
    private DatabaseReference reference;
    private LocationManager manager;
    private final int MIN_TIME = 1000; //1sec
    private final int MAX_DISTANCE = 1; //1meter
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    int markerMaps;
    ModelAccess profile;
    FirebaseDatabase database;
    DatabaseReference references;
    private RequestQueue mRequestQueue;
    ProgressDialog progressDialog;
    float jarakaDriverNow;
    String key;
    String bloodType = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_find, container, false);

        mRequestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        profile = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        database = FirebaseDatabase.getInstance();
        references = database.getReference("location");

//        if (!Utils.isLoggedIn()) {
//            bloodTypeUser = profile.getBloodtype();
//            Log.d("GOLONGAN", bloodTypeUser);
//        }

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_google);
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getAllData();
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        return v;
    }

    private void getAllData() {
        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();

        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.showUser, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            boolean status = response.getBoolean("error");
                            if (status == false) {
                                Log.d("data driver = ", response.toString());
                                String data = response.getString("data");
                                JSONArray arayData = new JSONArray(data);
                                for (int i = 0; i < arayData.length(); i++) {
                                    JSONObject jsonObject = arayData.getJSONObject(i);
                                    final ModelAccess driver = new ModelAccess();
                                    final String roles = jsonObject.getString("role");
                                    final String _id = jsonObject.getString("_id");
                                    final String fullname = jsonObject.getString("fullname");
                                    final String phone = jsonObject.getString("phone");
                                    final String photoProfile = jsonObject.getString("profilephoto");
                                    bloodType = jsonObject.getString("bloodtype");
                                    final String address = jsonObject.getString("address");

                                    if (roles.equals("2")) {
                                        driver.setFullname(fullname);
                                        driver.setPhone(phone);
                                        driver.setProfilephoto(photoProfile);
                                        driver.set_id(_id);
                                        if (bloodType.equals("A")) {
                                            markerMaps = R.drawable.ic_blood_type_a;
                                        } else if (bloodType.equals("B")) {
                                            markerMaps = R.drawable.ic_blood_type_b;
                                        } else if (bloodType.equals("AB")) {
                                            markerMaps = R.drawable.ic_blood_type_ab;
                                        } else if (bloodType.equals("O")) {
                                            markerMaps = R.drawable.ic_blood_type_o;
                                        } else {
                                            googleMaps.setMyLocationEnabled(true);
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                hideDialog();
            }
        });
        mRequestQueue.add(req);
    }

    private void getCurrentLocation() {
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(final GoogleMap googleMap) {

                            googleMaps = googleMap;
                            final LatLng nowLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            if (references != null) {
                                final Map<String, Marker> mNamedMarkers = new HashMap<String, Marker>();
                                references.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        if (!snapshot.equals(null)) {
                                            key = snapshot.getKey();
                                            Log.d("KEY", key);
                                            if (snapshot.hasChild("latitude") && snapshot.hasChild("longitude")) {
                                                double latNew = snapshot.child("latitude").getValue(Double.class);
                                                double lngNew = snapshot.child("longitude").getValue(Double.class);
                                                double latLast = location.getLatitude();
                                                double lngLast = location.getLongitude();
                                                String fullname = snapshot.child("fullname").getValue(String.class);
                                                String address = snapshot.child("address").getValue(String.class);
                                                final String birthdate = snapshot.child("birthdate").getValue(String.class);
                                                final String phone = snapshot.child("phone").getValue(String.class);
                                                final String email = snapshot.child("email").getValue(String.class);

                                                final float result[] = new float[10];
                                                Location.distanceBetween(latLast, lngLast, latNew, lngNew, result);
                                                float distanceLocation = result[0] / 1000;
                                                float resultLocation = (float) (Math.round(distanceLocation * 100)) / 100;
                                                jarakaDriverNow = resultLocation;

                                                LatLng newLocation = new LatLng(latNew, lngNew);
                                                Marker marker = mNamedMarkers.get(key);

                                                if (googleMaps != null) {
                                                    googleMaps.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                                        @Override
                                                        public View getInfoWindow(Marker marker) {
                                                            return null;
                                                        }

                                                        @Override
                                                        public View getInfoContents(Marker marker) {
                                                            View v = getLayoutInflater().inflate(R.layout.info_window, null);
                                                            ImageView iconGolongan = v.findViewById(R.id.golonganPengguna);
                                                            TextView namaPengguna = v.findViewById(R.id.namaPengguna);
                                                            TextView tanggalLahirPengguna = v.findViewById(R.id.tanggalLahir);
                                                            TextView nomorTeleponPengguna = v.findViewById(R.id.nomorTelepon);
                                                            TextView emailPengguna = v.findViewById(R.id.emailPengguna);
                                                            TextView alamatPengguna = v.findViewById(R.id.alamatPengguna);
                                                            TextView statusPengguna = v.findViewById(R.id.statusDonor);
                                                            TextView usiaPengguna = v.findViewById(R.id.usia);

                                                            namaPengguna.setText(marker.getTitle());
                                                            alamatPengguna.setText(marker.getSnippet());
                                                            iconGolongan.setImageResource(markerMaps);

                                                            Calendar calendar = Calendar.getInstance();
                                                            Calendar dob = Calendar.getInstance();
                                                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                                                            try {
                                                                dob.setTime(sdf.parse(birthdate));
                                                                int age = calendar.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
                                                                if (calendar.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
                                                                    age--;
                                                                }
                                                                Integer ageInt = new Integer(age);
                                                                String ageString = ageInt.toString();
                                                                usiaPengguna.setText(ageString);
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }

                                                            tanggalLahirPengguna.setText(birthdate);
                                                            nomorTeleponPengguna.setText(phone);
                                                            emailPengguna.setText(email);
                                                            statusPengguna.setText("sudah donor");

                                                            return v;
                                                        }
                                                    });
                                                }

                                                if (marker == null) {
                                                    MarkerOptions options = getMarkerOption(key);
                                                    marker = googleMaps.addMarker(options.title(fullname).snippet(address).position(newLocation));
                                                    mNamedMarkers.put(key, marker);
                                                } else {
                                                    marker.setPosition(newLocation);
                                                }
                                            } else {
                                                System.out.println("Long or Lat were null!!!");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        if (!snapshot.equals(null)) {
                                            key = snapshot.getKey();
                                            if (snapshot.hasChild("latitude") && snapshot.hasChild("longitude")) {
                                                double latNew = snapshot.child("latitude").getValue(Double.class);
                                                double lngNew = snapshot.child("longitude").getValue(Double.class);
                                                double latLast = location.getLatitude();
                                                double lngLast = location.getLongitude();
                                                String fullname = snapshot.child("fullname").getValue(String.class);
                                                String address = snapshot.child("address").getValue(String.class);
                                                final String birthdate = snapshot.child("birthdate").getValue(String.class);
                                                final String phone = snapshot.child("phone").getValue(String.class);
                                                final String email = snapshot.child("email").getValue(String.class);

                                                final float result[] = new float[10];
                                                Location.distanceBetween(latLast, lngLast, latNew, lngNew, result);
                                                float distanceLocation = result[0] / 1000;
                                                float resultLocation = (float) (Math.round(distanceLocation * 100)) / 100;
                                                jarakaDriverNow = resultLocation;
                                                Log.d("DISTANCE", String.valueOf((jarakaDriverNow)));

                                                LatLng newLocation = new LatLng(latNew, lngNew);
                                                Marker marker = mNamedMarkers.get(key);

                                                if (googleMaps != null) {
                                                    googleMaps.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                                        @Override
                                                        public View getInfoWindow(Marker marker) {
                                                            return null;
                                                        }

                                                        @Override
                                                        public View getInfoContents(Marker marker) {
                                                            View v = getLayoutInflater().inflate(R.layout.info_window, null);
                                                            ImageView iconGolongan = v.findViewById(R.id.golonganPengguna);
                                                            TextView namaPengguna = v.findViewById(R.id.namaPengguna);
                                                            TextView tanggalLahirPengguna = v.findViewById(R.id.tanggalLahir);
                                                            TextView nomorTeleponPengguna = v.findViewById(R.id.nomorTelepon);
                                                            TextView emailPengguna = v.findViewById(R.id.emailPengguna);
                                                            TextView alamatPengguna = v.findViewById(R.id.alamatPengguna);
                                                            TextView statusPengguna = v.findViewById(R.id.statusDonor);
                                                            TextView usiaPengguna = v.findViewById(R.id.usia);

                                                            namaPengguna.setText(marker.getTitle());
                                                            alamatPengguna.setText(marker.getSnippet());
                                                            iconGolongan.setImageResource(markerMaps);

                                                            Calendar calendar = Calendar.getInstance();
                                                            Calendar dob = Calendar.getInstance();
                                                            SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
                                                            try {
                                                                dob.setTime(sdf.parse(birthdate));
                                                                int age = calendar.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
                                                                if (calendar.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
                                                                    age--;
                                                                }
                                                                Integer ageInt = new Integer(age);
                                                                String ageString = ageInt.toString();
                                                                usiaPengguna.setText(ageString);
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }

                                                            tanggalLahirPengguna.setText(birthdate);
                                                            nomorTeleponPengguna.setText(phone);
                                                            emailPengguna.setText(email);
                                                            statusPengguna.setText("sudah donor");

                                                            return v;
                                                        }
                                                    });
                                                }

                                                if (marker == null) {
                                                    MarkerOptions options = getMarkerOption(key);
                                                    marker = googleMaps.addMarker(options.position(newLocation));
                                                    mNamedMarkers.put(key, marker);
                                                } else {
                                                    marker.setPosition(newLocation);
                                                }
                                            } else {
                                                System.out.println("Long or Lat were null!!!");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                        String key = snapshot.getKey();
                                        Marker marker = mNamedMarkers.get(key);
                                        if (marker != null)
                                            marker.remove();
                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        Log.d("PRIORITY FOR", snapshot.getKey());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            googleMap.setMinZoomPreference(15.0f);
                            googleMap.setMaxZoomPreference(20.0f);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nowLocation, 16.0f));
                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                            googleMap.setMyLocationEnabled(true);
                            googleMap.setPadding(0, 100, 0, 150);
                        }
                    });
                }
            }
        });
    }

    private MarkerOptions getMarkerOption(String key) {
        MarkerOptions options = new MarkerOptions();
        options.icon(bitmapDescriptor(getActivity(), markerMaps));
        return options;
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId) {
        Drawable drawable = ContextCompat.getDrawable(context, vectorResId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    private void showDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void hideDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
