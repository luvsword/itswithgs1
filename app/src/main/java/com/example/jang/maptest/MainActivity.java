package com.example.jang.maptest;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
/**
 * 현재 위치의 지도를 보여주는 방법에 대해 알 수 있습니다.
 *
 * Google Play Services 라이브러리를 링크하여 사용합니다.
 * 구글맵 v2를 사용하기 위한 여러 가지 권한이 있어야 합니다.
 * 매니페스트 파일 안에 있는 키 값을 PC에 맞는 것으로 새로 발급받아서 넣어야 합니다.
 *
 * @author Mike
 */
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.jang.maptest.helper.RequestCapture;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    static final LatLng SEOUL = new LatLng(37.56, 126.97);
    private static final String TAG = "@@@";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final int REQUEST_CODE_LOCATION = 2;
    private double locationX;
    private double locationY;

    private GoogleMap googleMap;

    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

        Marker seoul = googleMap.addMarker(new MarkerOptions().position(SEOUL)
                .title("Seoul"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom( SEOUL, 15));

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1500);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CheckPermission();
        }

        startLocationUpdates();
    }


    private void CheckPermission() {

        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_LOCATION);

                return;
            }
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.WRITE_CONTACTS},
                    REQUEST_CODE_LOCATION);
            return;
        }

        hasWriteContactsPermission = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION);

                return;
            }
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.WRITE_CONTACTS},
                    REQUEST_CODE_LOCATION);
            return;
        }
        ;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng CURRENT_LOCATION = new LatLng(location.getLatitude(), location.getLongitude());
        locationX = location.getLatitude();
        locationY = location.getLongitude();

        Log.d(TAG, "location changed x = " + locationX + ", y = " + locationY);
        googleMap.clear();
        Marker seoul = googleMap.addMarker(new MarkerOptions().position(CURRENT_LOCATION)
                .title("Seoul"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION, 15));

        updateEvent("MOVING_START");
    }

    private void updateEvent(String event) {
        RequestCapture epcis = new RequestCapture();

        String eventDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format((System.currentTimeMillis()));
        String eventTime = new java.text.SimpleDateFormat("HH:mm:ss").format((System.currentTimeMillis()));

        Log.d(TAG, "updateEvent");

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<!DOCTYPE project>\n" +
                "<epcis:EPCISDocument xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\" \n" +
                "                     schemaVersion=\"1.2\" creationDate=\"2016-11-13T11:30:47.0Z\">\n" +
                "  <EPCISBody>\n" +
                "    <EventList>\n" +
                "      <ObjectEvent>\n" +
                "        <!-- When -->\n" +
                "        <eventTime>" + eventDate + "T" + eventTime + ".116-10:00</eventTime>\n" +
                "        <eventTimeZoneOffset>-10:00</eventTimeZoneOffset>\n" +
                "        <!-- When! -->\n" +
                "\n" +
                "        <!--  What -->\n" +
                "        <epcList>\n" +
                "          <epc>urn:epc:id:sgtin:0614141.112345.12345</epc>\n" +
                "        </epcList>\n" +
                "        <!-- What!-->\n" +
                "\n" +
                "        <!-- Add, Observe, Delete -->\n" +
                "        <action>ADD</action>\n" +
                "\n" +
                "        <!-- Why -->\n" +
                "        <bizStep>urn:epcglobal:cbv:bizstep:"+ event +"</bizStep>\n" +
                "        <disposition>urn:epcglobal:cbv:disp:user_accessible</disposition>\n" +
                "        <!-- Why! -->\n" +
                "\n" +
                "        <!-- Where -->\n" +
                "        <bizLocation>\n" +
                "          <id>urn:epc:id:sgln:7654321.54321.1234</id>\n" +
                "          <extension>\n" +
                "            <geo>" + locationX + "," + locationY + "</geo>\n" +
                "          </extension>\n" +
                "        </bizLocation>\n" +
                "        <!-- Where! -->\n" +
                "      </ObjectEvent>\n" +
                "    </EventList>\n" +
                "  </EPCISBody>\n" +
                "</epcis:EPCISDocument>";
        epcis.execute(xml);
    }
    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            Log.d(TAG, "Start Location update");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            Toast.makeText(this,"Location Unavialable",Toast.LENGTH_LONG).show();
        }


    }



}


