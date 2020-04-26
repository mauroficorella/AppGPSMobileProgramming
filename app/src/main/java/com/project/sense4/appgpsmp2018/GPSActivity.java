package com.project.sense4.appgpsmp2018;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GPSActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private Context mContext;
    private EditText latitudine;
    private EditText longitudine;
    private Button getPosBtn;
    private Button btnCancelRequest;
    private ProgressBar progressLocationBar;
    private TextView tvSearchingGPS;
    static final int REQUEST_LOCATION = 1; //costante per identificare la richiesta di permesso


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        mContext=this;
        latitudine = (EditText) findViewById(R.id.LocationLat);
        longitudine = (EditText) findViewById(R.id.LocationLong);
        progressLocationBar = (ProgressBar) findViewById(R.id.progressLocationBar);
        tvSearchingGPS = (TextView) findViewById(R.id.tvSearchingGPS);
        progressLocationBar.setVisibility(View.GONE);
        tvSearchingGPS.setVisibility(View.GONE);
        getPosBtn = (Button)findViewById(R.id.getPosBtn);
        getPosBtn.setOnClickListener(new GetPosBtnListener(this.latitudine, this.longitudine));
        btnCancelRequest = (Button)findViewById(R.id.btnCancelRequest) ;
        btnCancelRequest.setOnClickListener(new CancelRequestBtnListener());
        btnCancelRequest.setVisibility(View.GONE);
        locationManager=(LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            getLastLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            isLocationEnabled();
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (locationManager != null)    {
                locationManager.removeUpdates(locationListenerGPS);
            }
            isLocationEnabled();
        }
    };

    private void getLastLocation(Location location) {
        double latitude=location.getLatitude();
        double longitude=location.getLongitude();
        tvSearchingGPS.setVisibility(View.GONE);
        progressLocationBar.setVisibility(View.GONE);
        btnCancelRequest.setVisibility(View.GONE);
        latitudine.setText("" + latitude + "°");
        longitudine.setText("" + longitude + "°");
        String msg=getString(R.string.search_completed);
        Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
        locationManager.removeUpdates(locationListenerGPS);
    }


    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause() { //La richiesta della posizione viene bloccata per salvaguardare la batteria del dispositivo
        super.onPause();
        cancelRequest();
    }

    private boolean isLocationEnabled() {
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            tvSearchingGPS.setVisibility(View.GONE);
            progressLocationBar.setVisibility(View.GONE);
            btnCancelRequest.setVisibility(View.GONE);
            android.support.v7.app.AlertDialog.Builder alertDialog=new android.support.v7.app.AlertDialog.Builder(mContext);
            alertDialog.setTitle(getString(R.string.enable_localization));
            alertDialog.setMessage(getString(R.string.localization_disabled));
            alertDialog.setPositiveButton(getString(R.string.goto_settings), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton(getString(R.string.dontgoto_settings), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            android.support.v7.app.AlertDialog alert=alertDialog.create();
            alert.show();
            return false;
        }
        else{
            String msgGPSok = getString(R.string.gps_enabled);
            Toast.makeText(mContext, msgGPSok, Toast.LENGTH_LONG).show();
            return true;
        }
    }

    public void getLocation(Context context) {
        if(locationManager == null){
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_title)
                        .setMessage(R.string.dialog_message)
                        .setPositiveButton(R.string.permission_button_accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(GPSActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_LOCATION);
                            }
                        })
                        .setNegativeButton(R.string.permission_button_deny, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(GPSActivity.this,
                                        R.string.location_permission_denied_message,
                                        Toast.LENGTH_LONG).show(); //messaggio in basso a comparsa in caso di permesso negato
                            }
                        })
                        .show(); //creo il dialog e lo mostro
            } else {    //se non li hai mai negati, chiedi per la prima volta i permessi
                // ask user for permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }
        } else {    //se ho già i permessi --> verifico se il GPS è abilitato o no
            boolean isEnabled = isLocationEnabled();
            if(isEnabled){
                tvSearchingGPS.setVisibility(View.VISIBLE);
                progressLocationBar.setVisibility(View.VISIBLE);
                btnCancelRequest.setVisibility(View.VISIBLE);
                try{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void cancelRequest(){
        if (locationManager != null){
            locationManager.removeUpdates(locationListenerGPS);
            locationManager = null;
            progressLocationBar.setVisibility(View.GONE);
            tvSearchingGPS.setVisibility(View.GONE);
            btnCancelRequest.setVisibility(View.GONE);
        }
    }
}
