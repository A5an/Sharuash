package easy.life.sharuash.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import easy.life.sharuash.BuildConfig;
import easy.life.sharuash.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BestMapsFragment extends Fragment {
    Activity context;
    Boolean rassbian = false;
    GoogleMap googleMap;
    FusedLocationProviderClient fusedLocationClient;
    private double lat, lng;
    DatabaseReference databaseReference1;
    ValueEventListener eventListener;
    Handler handler = new Handler();
    List<Marker> markerList = new ArrayList<>();
    private boolean isMapReady = false;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {

            googleMap = googleMap;
            isMapReady = true;
            databaseReference1 = FirebaseDatabase.getInstance().getReference("Location");
            GoogleMap finalGoogleMap = googleMap;

            eventListener = databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (rassbian == false){
                                String loc = snapshot.getValue(String.class);
                                String[] latLngArray = loc.split(",");
                                double latitude = Double.parseDouble(latLngArray[0].trim());
                                double longitude = Double.parseDouble(latLngArray[1].trim());
                                Marker marker1 = finalGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitude, longitude))
                                        .title("animal"));
                                markerList.add(marker1);
                                rassbian = true;
                            }
                            else if(rassbian == true){
                                Marker markerToDelete = markerList.get(0);
                                markerToDelete.remove();
                                markerList.remove(markerToDelete);
                                rassbian = false;
                            }
                            handler.postDelayed(this, 1000);
                        }
                    }, 1000);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            OkHttpClient client = new OkHttpClient();

            String api_key = BuildConfig.GOG_API;

            StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            stringBuilder.append("location=" + lat + "," + lng);
            stringBuilder.append("&radius=6000");
            stringBuilder.append("&keyword=ветеренарная");
            stringBuilder.append("&sensor=true");
            stringBuilder.append("&key="+api_key);
            String url = stringBuilder.toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            GoogleMap finalGoogleMap1 = googleMap;
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String jsonResponse = response.body().string();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (finalGoogleMap1 != null) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(jsonResponse);
                                            JSONArray resultsArray = jsonObject.getJSONArray("results");

                                            for (int i = 0; i < resultsArray.length(); i++) {
                                                JSONObject placeObject = resultsArray.getJSONObject(i);
                                                JSONObject geometryObject = placeObject.getJSONObject("geometry");
                                                JSONObject locationObject = geometryObject.getJSONObject("location");
                                                double placeLat = locationObject.getDouble("lat");
                                                double placeLng = locationObject.getDouble("lng");
                                                String placeName = placeObject.getString("name");

                                                // Add markers for places
                                                finalGoogleMap1.addMarker(new MarkerOptions().position(new LatLng(placeLat, placeLng)).title(placeName));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    } else {

                    }
                }
            });

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        context = getActivity();
        return inflater.inflate(R.layout.fragment_best_maps, container, false);
    }
    public void onStart() {
        super.onStart();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        Places.initialize(context.getApplicationContext(), "AIzaSyASO95vCcQNQrzr0lERusMhR62QUjEMxB0");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(BestMapsFragment.this.context);
        googleMap = googleMap;


        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getCurrentLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (databaseReference1 != null && eventListener != null && isMapReady) {
            databaseReference1.addValueEventListener(eventListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (databaseReference1 != null && eventListener != null && isMapReady) {
            databaseReference1.removeEventListener(eventListener);
        }
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(BestMapsFragment.this.context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BestMapsFragment.this.context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                SupportMapFragment mapFragment =
                        (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.navigation);
                            String myloc = getString(R.string.mylocatoin);
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(myloc).icon(icon);
                            googleMap.addMarker(markerOptions);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        } else {
                            Toast.makeText(context, "Turn on permission!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }





}
