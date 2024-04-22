package com.example.convertisseur2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

/**
 * This class will manage the google MAP API feature
 * After passing the initial rate it will display on the map the country of destination
 */
public class GoogleMapAPI extends FragmentActivity implements OnMapReadyCallback {

   // GoogleMap mAPIinit;
    private static String TAG = "myApp";
    SupportMapFragment mapFragment;
    private String currencyNumberOne;
    private Button button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_google);

        // get data from modeOnline
        Intent intent = getIntent();
        currencyNumberOne = intent.getExtras().getString("currency localisation"); // get currency init
        button = (Button) findViewById(R.id.backToConversion);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToConvertionPage();
            }
        });
    }

    /**
     * This method will swtich back to online mode
     */
    private void switchToConvertionPage() {
        Intent changeToConvertionPage = new Intent(this, ConversionMainActivity.class);
        startActivity(changeToConvertionPage);
    }

    /**
     * Overrithed method called when implementation of OnMapReadyCallback interface
     * @param googleMap : google map variable
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        GoogleMap mAPIinit;
        mAPIinit = googleMap;
        getLocationOfFirstCurrency(mAPIinit);
    }

    /**
     * This method will analyse the value of the first Spinner and add the loc marker
     * button according to the currency
     * @param mAPIinit : google map attribut
     */
    private void getLocationOfFirstCurrency(GoogleMap mAPIinit) {
        if(Objects.equals(currencyNumberOne, "EURO")){
            LatLng euro = new LatLng(50.88478605555491, 4.344859995140608);
            mAPIinit.addMarker(new MarkerOptions().position(euro).title("europe"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(euro));
        } else if(Objects.equals(currencyNumberOne, "USD")){
            LatLng dollar = new LatLng(40.38394135489547, -98.18619562172326);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("USA"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "JPY")){
            LatLng dollar = new LatLng(35.77338669513396, 139.6139274250192);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("JPY"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "BGN")){
            LatLng dollar = new LatLng(42.67135942063904, 23.32513107068806);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("BGN"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "CZK")){
            LatLng dollar = new LatLng(50.06227957087554, 14.48500453862101);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("CZK"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "DKK")){
            LatLng dollar = new LatLng(55.68552444478129, 12.541827102557132);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("DKK"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "GBP")){
            LatLng dollar = new LatLng(51.501617456803565, -0.13374290065335884);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("GBP"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "HUF")){
            LatLng dollar = new LatLng(47.481654715340355, 19.0482948496079);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("HUF"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "PLN")){
            LatLng dollar = new LatLng(52.2374330121036, 20.99001640902875);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("PLN"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "RON")){
            LatLng dollar = new LatLng(44.41053414316275, 26.125248625048307);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("RON"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "SEK")){
            LatLng dollar = new LatLng(59.32318438836954, 18.073029587683706);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("SEK"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "CHF")){
            LatLng dollar = new LatLng(46.196547612968494, 6.141083303759177);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("CHF"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "ISK")){
            LatLng dollar = new LatLng(64.12802437846867, -21.911367454914856);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("ISK"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "NOK")){
            LatLng dollar = new LatLng(59.91963854459076, 10.752288253719781);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("NOK"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "HRK")){
            LatLng dollar = new LatLng(45.801099964342214, 16.024767406233668);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("HRK"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "TRY")){
            LatLng dollar = new LatLng(39.907853562551345, 32.85984674670479);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("TRY"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "AUD")){
            LatLng dollar = new LatLng(-37.79163650470943, 145.08774254688242);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("AUD"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "BRL")){
            LatLng dollar = new LatLng(-15.82005006744794, -47.92150806551498);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("BRL"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "CAD")){
            LatLng dollar = new LatLng(45.40072457498799, -75.65778993679548);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("CAD"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "CNY")){
            LatLng dollar = new LatLng(39.903848416409396, 116.38414060576191);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("CNY"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "HKD")){
            LatLng dollar = new LatLng(22.323888938905675, 114.1804646890461);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("HKD"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "IDR")){
            LatLng dollar = new LatLng(-5.151335186754662, 119.44366254518631);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("IDR"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "ILS")){
            LatLng dollar = new LatLng(31.76627629425369, 35.21773530259778);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("ILS"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "INR")){
            LatLng dollar = new LatLng(28.60171053770531, 77.19908488493402);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("INR"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "MXN")){
            LatLng dollar = new LatLng(19.421309558437912, -99.13583971884051);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("MXN"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "MYR")){
            LatLng dollar = new LatLng(3.126003743796139, 101.65974949758136);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("MYR"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "NZD")){
            LatLng dollar = new LatLng(-36.875130464603835, 174.74879050168684);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("NZD"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "PHP")){
            LatLng dollar = new LatLng(14.62135276450896, 120.998502030271);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("PHP"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "SGD")){
            LatLng dollar = new LatLng(1.3444476755444907, 103.86280529572302);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("SGD"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "THB")){
            LatLng dollar = new LatLng(13.773980806840562, 100.50303527955742);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("THB"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        }else if(Objects.equals(currencyNumberOne, "ZAR")){
            LatLng dollar = new LatLng(-25.757362781652215, 28.224689489394695);
            mAPIinit.addMarker(new MarkerOptions().position(dollar).title("ZAR"));
            mAPIinit.moveCamera(CameraUpdateFactory.newLatLng(dollar));
        } else{
            Toast.makeText(GoogleMapAPI.this, "Problème lors de l'initialisation", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    /**
     *
     */
    // LIFE CYCLE OF APP :)
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "App started");
    }

    /**
     *
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "App stoped");
    }

    /**
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "App paused");
    }

    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "App destroyed");
    }

}
