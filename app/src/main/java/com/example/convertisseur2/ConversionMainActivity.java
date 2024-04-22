package com.example.convertisseur2;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This class manages all processes relatives to online mode
 * Parse XML, convert manages the Google MAP API, geo-localisation feature
 * and firebase management
 */
public class ConversionMainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LocationListener {

    // tag for debugging
    public static final String TAG = "Myapp";

    // private variables
    private String mtheNumber;
    private float mtheNumberToDouble;
    private float mtoDollar;
    private EditText myNumber;
    private TextView myResultInDollar;
    private String mcurrencyTXTinit;
    private String mcurrencyTXTDest;

    // timer management variables to test timer put 60000
    private static final long START_TIME_IN_MILLIS = 3600000;
    private TextView myTimer;
    private boolean mTimerRunning;
    private long mTimeLeftBeforeUpdate = START_TIME_IN_MILLIS;
    private long mhours;
    private long minutes;
    private long mseconds;
    Thread timerThread;

    // array lists
    ArrayList<String> arrayListCurrency = new ArrayList<String>();
    ArrayList<String> arrayListRate = new ArrayList<>();

    // hashMaps
    private HashMap<String, String> myHashEuro = new HashMap<>();

    // database variable
    private DatabaseReference databaseReference;

    // geo localisation variables
    private double mlongitude;
    private double mlatitude;
    private String mgetCountryCode;



    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "app created");
        timerThread = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        setContentView(R.layout.mode_on_line);
        Button switchActivity = (Button) this.findViewById(R.id.button4);
        Button retractFirebase = (Button) this.findViewById(R.id.readFirebaseOp);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        myTimer = (TextView) findViewById(R.id.startCountdown);

        // country and localisation
        locationAndInternetEnabled(); // check location is activated
        mgetCountryCode = geoLocalisationManager(); // get country of localisation

        Log.d(TAG, "the country after processing is " + mgetCountryCode);
        excecuteTheTimer();
        updateData();
        switchActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methodeToSwitch();
            }
        });

        retractFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "inside on click");
                methodToRead();
            }
        });
    }

    private void methodToRead(){
        Intent switchreadIntent = new Intent(this, ReadFirebase.class);
        Log.d(TAG, "inside swith method");
        startActivity(switchreadIntent);
    }


    /**
     * This methode will check if the GPS and internet is enabled
     */
    private void locationAndInternetEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context. LOCATION_SERVICE ) ;
        boolean gps_enabled = false;
        boolean network_enabled = false;

        // do the checking
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER  ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if(gps_enabled && network_enabled){
            Toast.makeText(getApplicationContext(),"location and network are  on",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),"location and network are  off",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginAndRegisterFirebase.class);
            startActivity(intent);
        }
    }


    /**
     * This method resumes the process of the geo-localisation
     * It will check IF the autorizations has been accepted and return the country name thanks to methods
     * onLocationChanged() and getCountryName()
     * the country name
     * @return country name according to geo-localisation
     */
    @Nullable
    private String geoLocalisationManager() {
        // location variables
        LocationManager mlocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(this, LoginAndRegisterFirebase.class);
            startActivity(intent);
           // return null;
        }
        Location location = mlocationManager.getLastKnownLocation(mlocationManager.NETWORK_PROVIDER);

        onLocationChanged(location);
        String mcountry = getCountryName(location);
        return mcountry;
    }

    /**
     * This method will get the latitude and longitude of the telephone
     * before going in a method to determine the country name
     * @param mlocation
     */
    @Override
    public void onLocationChanged(@NonNull Location mlocation) {
        mlongitude = mlocation.getLongitude();
        mlatitude = mlocation.getLatitude();
        getCountryName(mlocation);
    }


    /**
     * This method will determin the country name according to the values of
     * the logitude and latidude
     * @param location
     * @return country name
     */
    private String getCountryName(Location location){
        try {
            Geocoder mgeocoder=new Geocoder(this);
            List<Address> maddresses=null;
            maddresses = mgeocoder.getFromLocation(mlatitude, mlongitude,1);
            String mcountryCode = maddresses.get(0).getCountryName();
            return mcountryCode;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * This method will excecute a countdown timer when the app run's
     * Every hour, the from the firebase will be updated
     */
    private void excecuteTheTimer() {

        // String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        // update the firebase and parse XML data
        // restart the timer
        CountDownTimer mcountDownTimer = new CountDownTimer(mTimeLeftBeforeUpdate, 100) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                mTimeLeftBeforeUpdate = l;
                NumberFormat f = new DecimalFormat("00");
                mhours = (mTimeLeftBeforeUpdate / 3600000) % 24;
                minutes = (mTimeLeftBeforeUpdate / 60000) % 60;
                mseconds = (mTimeLeftBeforeUpdate / 1000) % 60;
                // String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                myTimer.setText(f.format(mhours) + ":" + f.format(minutes) + ":" + f.format(mseconds));
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateData(); // update the firebase and parse XML data
                start(); // restart the timer
            }
        }.start();
        mTimerRunning = true;
    }


    /**
     * this methode will parse the date from the XML file
     * store the data in a hashmap and store the values of the
     * hashmap in the firebase
     */
    private void updateData() {
        ArrayList<String> lstOfCountries= new ArrayList<>();


        // parse xml file in background
        BackgroundParsing xmlBackground = new BackgroundParsing();
        xmlBackground.execute();
        xmlBackground.doInBackground(); // excecuted in backgroud..

        // write and update firebase
        for(int i = 0; i<33; i++){
            Log.d(TAG, arrayListCurrency.get(i) +  "" + arrayListRate.get(i));
            writeNewData(arrayListCurrency.get(i), arrayListCurrency.get(i),arrayListRate.get(i));
        }
        Toast.makeText(getApplicationContext(),"firebase updated",Toast.LENGTH_SHORT).show();
    }

    /**
     * This method will switch the login page of the firebase
     */
    private void methodeToSwitch(){
        Intent switchActivityIntent = new Intent(this, LoginAndRegisterFirebase.class);
        startActivity(switchActivityIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
      //  Intent intent = getIntent();

        // buttons
        Button myButtonConvert = (Button) this.findViewById(R.id.button3);
        Button mupdateDataNow = (Button) findViewById(R.id.updateData);

        // textView
        this.myNumber = (EditText) this.findViewById(R.id.myEnterNumber);
        this.myResultInDollar = (TextView) this.findViewById(R.id.textView);

        // Spinners
        Spinner mySpinnerMoneyInit = (Spinner) this.findViewById(R.id.mySpinnerCurrencyInit);
        Spinner mySpinnerMoneyFinal = (Spinner) this.findViewById(R.id.mySpinnerCurrencyDestination);

        ArrayAdapter<CharSequence> adapterInit = ArrayAdapter.createFromResource(this, R.array.currenceChoice, android.R.layout.simple_spinner_item);
        adapterInit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinnerMoneyInit.setAdapter(adapterInit);
        mySpinnerMoneyInit.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapterFinal = ArrayAdapter.createFromResource(this, R.array.currencySelection, android.R.layout.simple_spinner_item);
        adapterInit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinnerMoneyFinal.setAdapter(adapterInit);
        mySpinnerMoneyFinal.setOnItemSelectedListener(this);

         configurationSpinnerLocation(mgetCountryCode, mySpinnerMoneyFinal);

         // update the data now
        mupdateDataNow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateData();
            }
        });


        // lorsqu'on appui sur le bouton
        myButtonConvert.setOnClickListener(new View.OnClickListener() {
            /**
             * This method will manage what happens when we click on the convert button
             * 7 possibilities are possible
             * @param view
             */
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                Log.d(TAG,"test");
                myResultInDollar.setText(String.valueOf(mtoDollar));
                if(TextUtils.isEmpty(myNumber.getText().toString())){ // nothing is entered
                    Toast.makeText(ConversionMainActivity.this, "vous avez rien rentré", Toast.LENGTH_SHORT).show();
                }else if (Objects.equals((mcurrencyTXTinit), "Choose the currency")) { // init currency empty
                    Toast.makeText(ConversionMainActivity.this, "veuillez choisir une currency de départ", Toast.LENGTH_SHORT).show();
                } else if (Objects.equals((mcurrencyTXTDest), "Choose the currency")) { // final currency empty
                    Toast.makeText(ConversionMainActivity.this, "veuillez choisir une currency d'arrivée", Toast.LENGTH_SHORT).show();
                } else if(Objects.equals((mcurrencyTXTinit), mcurrencyTXTDest)){ // conversion between same currencys
                    Toast.makeText(ConversionMainActivity.this, "vous avez choisit la meme devise", Toast.LENGTH_SHORT).show();
                }else if (Objects.equals(mcurrencyTXTinit, "EURO")) { // from euro to currency
                    mtheNumber = myNumber.getText().toString(); // récupération du EditString
                    mtheNumberToDouble = Float.parseFloat(mtheNumber);// convert to double
                    String myHashValue = myHashEuro.get(mySpinnerMoneyFinal.getSelectedItem().toString()); // get value of Spinner
                    Float theHashToDouble = Float.parseFloat(myHashValue);// convert to double
                    myResultInDollar.setText(String.valueOf(mtheNumberToDouble * theHashToDouble));
                    Log.d(TAG, "test mySpinner get" + mySpinnerMoneyFinal.getSelectedItem().toString());
                    Log.d(TAG, myHashEuro.get("USD"));
                    changeToAPI(mySpinnerMoneyInit.getSelectedItem().toString()); // go to google MAP API class
                } else if(Objects.equals(mcurrencyTXTDest, "EURO")){ // currency to euro
                    mtheNumber = myNumber.getText().toString(); // from currency to euro
                    mtheNumberToDouble = Float.parseFloat(mtheNumber);// convert to double
                    String myHashValue = myHashEuro.get(mySpinnerMoneyInit.getSelectedItem().toString()); // get value of Spinner
                    Float theHashToDouble = Float.parseFloat(myHashValue); // convert to double
                    myResultInDollar.setText(String.valueOf(mtheNumberToDouble / theHashToDouble));
                    changeToAPI(mySpinnerMoneyInit.getSelectedItem().toString()); // go to google MAP API class
                }else { // currency to currency
                    mtheNumber = myNumber.getText().toString(); // récupération du EditString
                    mtheNumberToDouble = Float.parseFloat(mtheNumber);// convert to double
                    String myHashValueInit = myHashEuro.get(mySpinnerMoneyInit.getSelectedItem().toString()); // get value of Spinner
                    String myHashValueDest = myHashEuro.get(mySpinnerMoneyFinal.getSelectedItem().toString()); // get value of Spinner
                    Float theHashToDoubleInit = Float.parseFloat(myHashValueInit);// convert to double
                    Float theHashToDoubleDest = Float.parseFloat(myHashValueDest);// convert to double
                    mtoDollar = (float) ((mtheNumberToDouble * theHashToDoubleDest)/theHashToDoubleInit);
                    myResultInDollar.setText(String.valueOf(mtoDollar));
                    changeToAPI(mySpinnerMoneyInit.getSelectedItem().toString()); // go to google map API class
                }
            }
        });
    }

    /**
     * this function will set the destination currency spinner according to the country
     * where is located the telephone.
     * Note : if your are in a country witch is not reconized you will only be able to convert to
     * euros only
     * @param name : the country name
     * @param lastSpinner : the name of the last Spinner
     */
    // configuration of the destination spinner according to localisation of the phone
    private void configurationSpinnerLocation(String name, Spinner lastSpinner) {
        switch (name) {
            case "United States":
                lastSpinner.setSelection(1);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Mexico":
                lastSpinner.setSelection(2);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Japan":
                lastSpinner.setSelection(3);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Bulgaria":
                lastSpinner.setSelection(5);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Czech Republic":
                lastSpinner.setSelection(6);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Danemark":
                lastSpinner.setSelection(7);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "United Kingdom":
                lastSpinner.setSelection(8);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Hungary":
                lastSpinner.setSelection(9);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Poland":
                lastSpinner.setSelection(10);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Romania":
                lastSpinner.setSelection(11);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Sweden":
                lastSpinner.setSelection(12);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Switzerland":
                lastSpinner.setSelection(13);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Iceland":
                lastSpinner.setSelection(14);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Norway":
                lastSpinner.setSelection(15);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Croatia":
                lastSpinner.setSelection(16);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Turkey":
                lastSpinner.setSelection(17);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Australia":
                lastSpinner.setSelection(18);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Brazil":
                lastSpinner.setSelection(19);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Canada":
                lastSpinner.setSelection(20);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "China":
                lastSpinner.setSelection(21);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Hong Kong":
                lastSpinner.setSelection(22);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Indonesia":
                lastSpinner.setSelection(23);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Israel":
                lastSpinner.setSelection(24);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "India":
                lastSpinner.setSelection(25);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Korea, Republic Of":
                lastSpinner.setSelection(26);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Malaysia":
                lastSpinner.setSelection(27);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "New Zealand":
                lastSpinner.setSelection(28);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Philippines":
                lastSpinner.setSelection(29);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Singapore":
                lastSpinner.setSelection(30);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Thailand":
                lastSpinner.setSelection(31);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "South Africa":
                lastSpinner.setSelection(32);
                lastSpinner.setEnabled(false); // freezes the spinner
                break;
            case "Austria":
            case "Belgium":
            case "Republic of Cyprus":
            case "Denmark":
            case "Estonia":
            case "Finland":
            case "France":
            case "Germany":
            case "Greece":
            case "Ireland":
            case "Italy":
            case "Latvia":
            case "Lithuania":
            case "Luxembourg":
            case "Malta":
            case "Netherlands":
            case "Portugal":
            case "Slovakia":
            case "Slovenia":
            case "Spain":  // if country from euro zone is choosen
                lastSpinner.setSelection(4);
                lastSpinner.setEnabled(false); // freezes the spinner
            default:
                break;
        }
    }


    /**
     * This method will write and update the firebase thanks to setValue()
     * @param currency : currency from arrayList
     * @param rate : rate from arrayList
     */
    // will write the currency and rate in the firebase
    private void writeNewData(String currencyID, String currency, String rate) {
        FirebaseData firebaseData = new FirebaseData(currency, rate);
        //databaseReference.child("currency and rate").child(currencyID).setValue(firebaseData);
       // databaseReference.child("currency data").child(currencyID).child("currency and rate").setValue(firebaseData);
       HashMap<String, Object> map = new HashMap<>();
       map.put(currency, rate);
        FirebaseDatabase.getInstance().getReference().child("currency data").child(currencyID).updateChildren(map);
        //FirebaseDatabase.getInstance().getReference().child("currency and rates").setValue(currency);


    }

    /**
     * this method will create a intent to switch to google map API activity
     * and will passe the value of spinner
     * @param firstCurrency : value of currency destination
     */
    // change to API google main activity
    private void changeToAPI(String firstCurrency) {
        Intent changeToAPI = new Intent(this, GoogleMapAPI.class);
        changeToAPI.putExtra("currency localisation", firstCurrency);
        startActivity(changeToAPI);
    }


    /**
     * LIFE CYCLE OF APP:
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "activity destroyed");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "activity resumed");
    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "activity on pause");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "activity stoped");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "activity restarted");
    }


    /**
     * Manages the Spinner and the item selected on each spinner
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.mySpinnerCurrencyInit) { // select the init spinner
            mcurrencyTXTinit = adapterView.getItemAtPosition(i).toString();
        } else if (adapterView.getId() == R.id.mySpinnerCurrencyDestination) { // select the destination spinner
            mcurrencyTXTDest = adapterView.getItemAtPosition(i).toString();
        }
    }

    /**
     * @param adapterView
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d(TAG,"no spinner selected");
    }





    /**
     * This class will manage the background task of parsing the
     * xml data
     */
    public class BackgroundParsing extends AsyncTask<String, String, String> {

        /**
         * this overrited method will be excecuted during the background process
         * @param strings
         * @return null
         */
        @Override
        protected String doInBackground(String... strings) {
            String rateFromXML = null;
            try {
                Log.d(TAG, "In the Background task to work on XML file");
                // parse XML files and put data in nodeLists
                DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder xmlBuilder = null;
                xmlBuilder = xmlFactory.newDocumentBuilder();
                assert xmlBuilder != null;
                Document xmlFile = xmlBuilder.parse("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
                NodeList myCubes = xmlFile.getElementsByTagName("Cube"); // extract nodes "Cube"
                for (int i = 0; i < myCubes.getLength() ; i++) {
                    Node data = myCubes.item(i);
                    Element currency = (Element) data;
                    Element currencyValue = (Element) data;
                    arrayListCurrency.add(currency.getAttribute("currency"));
                    arrayListRate.add(currencyValue.getAttribute("rate"));
                    myHashEuro.put(arrayListCurrency.get(i).toString(), arrayListRate.get(i).toString());// put currency and rate in a hash map

                }

            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}