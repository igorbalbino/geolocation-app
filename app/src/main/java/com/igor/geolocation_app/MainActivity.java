package com.igor.geolocation_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] permissoesRequerida = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.INTERNET
    };

    public static final int APP_PERMISSOES_ID = 2023;

    private TextView txtValLatitude, txtValLongitude;

    private double longitude, latitude;
    private boolean gpsAtivo = false;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtValLatitude = findViewById(R.id.txtValLatitude);
        txtValLongitude = findViewById(R.id.txtValLongitude);

        locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);

        gpsAtivo = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        validaGpsAtivo();
    }

    private void validaGpsAtivo() {
        if(gpsAtivo) {
            obterCoordenadas();
        } else {
            longitude = 0.00;
            latitude = 0.00;

            Toast.makeText(this, "Coordenadas não disponíveis", Toast.LENGTH_SHORT).show();
        }
    }

    private void obterCoordenadas() {

        boolean permissaoGps = obterPermissaoGps();

        if(permissaoGps) {
            ultimaPosicaoValida();
        } else {
            obterPermissaoGps();
        }
    }

    @SuppressLint("MissingPermission")
    private void ultimaPosicaoValida() {

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(MainActivity.this, "Coordenadas obtidas com sucesso", Toast.LENGTH_SHORT).show();
                txtValLatitude.setText(String.valueOf(formatarGeopoint(latitude)));
                txtValLongitude.setText(String.valueOf(formatarGeopoint(longitude)));
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
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, locationListener);
    }

    private String formatarGeopoint(double val) {
        DecimalFormat decimalFormat = new DecimalFormat("#.######");

        return decimalFormat.format(val);
    }

    private boolean obterPermissaoGps() {
        Toast.makeText(this, "Aplicativo não possui permissão de acesso no GPS", Toast.LENGTH_SHORT).show();

        List<String> permissoesNegadas = new ArrayList<>();

        int permissaoNegada;

        for(String permissao : this.permissoesRequerida) {
            permissaoNegada = ContextCompat.checkSelfPermission(MainActivity.this, permissao);

            if(permissaoNegada != PackageManager.PERMISSION_GRANTED) {
                permissoesNegadas.add(permissao);
            }
        }

        if(!permissoesNegadas.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissoesNegadas.toArray(new String[permissoesNegadas.size()]),
                    APP_PERMISSOES_ID);
            return false;
        }

        return true;
    }
}