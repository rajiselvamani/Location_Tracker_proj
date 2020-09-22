package com.androidtutorialshub.loginregister.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.androidtutorialshub.loginregister.R;
import com.androidtutorialshub.loginregister.model.User;
import com.androidtutorialshub.loginregister.sql.DatabaseHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import android.location.Location;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by Raji on 09/22/2020.
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker, stLocationMarker;
    User user, user_retrieved;
    DatabaseHelper databaseHelper;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    String locationstring = "-";
    Button upateDB;
    TextView textInputEditTextEmail, textInputEditTextPassword, textInputEditTextName;
    int retrieved_id;
    String email_tosearch;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        locationstring = intent.getStringExtra("location");

        initViews();
        initListeners();
        initObjects();
        searchbyeamil();

    }

    /**
     * Below re the methods to display google map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                mMap.setMyLocationEnabled(true);

            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    /**
     * This method get current user location place it in Map
     */

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;


        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("current location of " + locationstring.split("-")[2]);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        updatelocation(mLastLocation);


      /*  //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }*/
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    /**
     * This method is to place stored location in google map
     */
    public void updatelocation(Location current) {
        Location location_stored = new Location("");
        location_stored.setLatitude(Double.valueOf(locationstring.split("-")[0]));
        location_stored.setLongitude(Double.valueOf(locationstring.split("-")[1]));
        //Place current location marker
        LatLng latLngst = new LatLng(location_stored.getLatitude(), location_stored.getLongitude());
        MarkerOptions markerOptionsst = new MarkerOptions();
        markerOptionsst.position(latLngst);
        markerOptionsst.title("stored location of " + locationstring.split("-")[2]);
        markerOptionsst.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        stLocationMarker = mMap.addMarker(markerOptionsst);
        validate_user_location(location_stored,current);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngst));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    /**
     * This method is to initialize views
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initViews() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        textInputEditTextEmail = (TextView) findViewById(R.id.textInputEditTextEmail);
        textInputEditTextEmail.setText(locationstring.split("-")[2]);
        textInputEditTextPassword = (TextView) findViewById(R.id.textInputEditTextPassword);
        textInputEditTextName = (TextView) findViewById(R.id.textInputEditTextName);
        upateDB = (Button) findViewById(R.id.appCompatButtonUpdate);

    }

    /**
     * This method is to initialize listeners
     */
    private void initListeners() {
        //set click event of login button
        upateDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_record();
                Toast.makeText(MapActivity.this,"User Record Updated",Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(this);
        user = new User();
        user_retrieved = new User();

    }



    /**
     * This method is to read new record from user and update on DB
     */
    private void update_record() {
        user.setName(textInputEditTextName.getText().toString().trim());
        user.setEmail(textInputEditTextEmail.getText().toString().trim());
        user.setPassword(textInputEditTextPassword.getText().toString().trim());
        user.setId(retrieved_id);
        user.setLatitude(locationstring.split("-")[0].trim());
        user.setLongitude(locationstring.split("-")[1].trim());
        databaseHelper.updateUser(user);
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
        textInputEditTextName.setText(null);

    }
    /**
     * This method is to search record in DB with given mail and update view with the values
     * retrieved from DB
     */
    private void searchbyeamil() {

        // AsyncTask is used that SQLite operation not blocks the UI Thread.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                user_retrieved=new User();
                email_tosearch=locationstring.split("-")[2].trim();
                user_retrieved=databaseHelper.search_by_email(email_tosearch);

                return null;
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                textInputEditTextPassword.setText(user_retrieved.getPassword());
                textInputEditTextName.setText(user_retrieved.getName());
                retrieved_id=user_retrieved.getId();
            }
        }.execute();

    }
    /**
     * This method is to validate user current location with stored location
     * to restrict DB update in stored location only
     */

          private void validate_user_location(Location location_stored,Location current)
           {
               final float[] distance = new float[1];
               Location.distanceBetween(location_stored.getLatitude(), location_stored.getLongitude(), current.getLatitude(), current.getLongitude(), distance);
               if (distance[0] < 200.0) {
                   upateDB.setEnabled(true);
                   textInputEditTextEmail.setEnabled(true);
                   textInputEditTextName.setEnabled(true);
                   textInputEditTextPassword.setEnabled(true);
               } else {
                   upateDB.setEnabled(false);
                   textInputEditTextEmail.setEnabled(false);
                   textInputEditTextName.setEnabled(false);
                   textInputEditTextPassword.setEnabled(false);
               }
           }

    }

