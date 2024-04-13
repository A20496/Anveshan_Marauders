package com.example.mymobilenetworktype;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView networkTypeTextView;
    private TextView signalStrengthTextView;
    private TextView bandwidthTextView;
    private TextView locationTextView;
    private TelephonyManager telephonyManager;
    private ConnectivityManager connectivityManager;
    private LocationManager locationManager;
    private static final int REQUEST_PHONE_STATE_PERMISSION = 1001;
    private static final int REQUEST_LOCATION_PERMISSION = 1002;
    private MapView mapView;
    private List<Location> locationList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextViews
        networkTypeTextView = findViewById(R.id.networkTypeTextView);
        signalStrengthTextView = findViewById(R.id.signalStrengthTextView);
        bandwidthTextView = findViewById(R.id.bandwidthTextView);
        locationTextView = findViewById(R.id.locationTextView);

        // Initialize TelephonyManager, ConnectivityManager, and LocationManager
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Initialize MapView
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setBuiltInZoomControls(true);

        // Request permission to access phone state if not granted already
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE_PERMISSION);
        } else {
            // Permission is already granted, proceed with initializing telephonyManager
            initializeTelephonyManager();
        }

        // Request permission to access location if not granted already
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission is already granted, proceed with initializing locationManager
            initializeLocationManager();
        }
    }

    private void initializeTelephonyManager() {
        // TelephonyManager initialization code (same as before)
    }

    private void initializeLocationManager() {
        // Create a LocationListener to listen for location updates
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Update location TextView with the new location coordinates
                locationTextView.setText("Location: " + location.getLatitude() + ", " + location.getLongitude());
                // Add the location to the list
                locationList.add(location);
                // Add marker to the map
                addMarkerToMap(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                // When location provider is enabled
                Toast.makeText(MainActivity.this, "Location provider " + provider + " enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                // When location provider is disabled
                Toast.makeText(MainActivity.this, "Location provider " + provider + " disabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // When location provider status changes
                switch (status) {
                    case LocationProvider.OUT_OF_SERVICE:
                        Toast.makeText(MainActivity.this, "Location provider " + provider + " out of service", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Toast.makeText(MainActivity.this, "Location provider " + provider + " temporarily unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationProvider.AVAILABLE:
                        Toast.makeText(MainActivity.this, "Location provider " + provider + " available", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        // Register the LocationListener to listen for location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            // Handle permissions
        }
    }

    private void addMarkerToMap(double latitude, double longitude) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(latitude, longitude));
        mapView.getOverlayManager().add(marker);
        mapView.invalidate(); // Refresh the map view
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_STATE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with initializing telephonyManager
                initializeTelephonyManager();
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with initializing locationManager
                initializeLocationManager();
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        }
    }

    // Other methods (getNetworkTypeString, getBandwidthString, getSignalStrength) remain the same



    private String getNetworkTypeString(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "Unknown";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO_0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO_A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO_B";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "EHRPD";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPAP";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "IDEN";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";
            default:
                return "Unknown";
        }
    }

    @NonNull
    private String getBandwidthString(int bandwidthKbps) {
        if (bandwidthKbps <= 0) {
            return "Unknown";
        } else if (bandwidthKbps <= 144) {
            return "2G GSM (~14.4 Kbps)";
        } else if (bandwidthKbps <= 268) {
            return "G GPRS (~26.8 Kbps)";
        } else if (bandwidthKbps <= 1088) {
            return "E EDGE (~108.8 Kbps)";
        } else if (bandwidthKbps <= 1280) {
            return "3G UMTS (~128 Kbps)";
        } else if (bandwidthKbps <= 3600) {
            return "H HSPA (~3.6 Mbps)";
        } else if (bandwidthKbps <= 23000) {
            return "H+ HSPA+ (~14.4 Mbps-23.0 Mbps)";
        } else if (bandwidthKbps <= 50000) {
            return "4G LTE (~50 Mbps)";
        } else {
            return "4G LTE-A (~500 Mbps)";
        }
    }

    private int getSignalStrength(SignalStrength signalStrength) {
        int signalStrengthValue = 0;
        if (signalStrength != null) {
            if (signalStrength.isGsm()) {
                signalStrengthValue = signalStrength.getGsmSignalStrength();
            } else {
                // For CDMA, use getCdmaDbm() or getCdmaEcio() instead
                // Here, we're using getCdmaDbm() for simplicity
                signalStrengthValue = signalStrength.getCdmaDbm();
            }
        }

        // Check if the device is in airplane mode
        boolean isAirplaneMode = Settings.Global.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;

        if (isAirplaneMode) {
            // If in airplane mode, return 0 dBm
            return 0;
        } else {
            // Otherwise, return 99 dBm
            return 99;
        }
    }
}
