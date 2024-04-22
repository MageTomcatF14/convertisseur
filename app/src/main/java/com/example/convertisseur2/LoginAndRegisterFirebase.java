package com.example.convertisseur2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *
 */
public class LoginAndRegisterFirebase extends AppCompatActivity {


    private static String TAG = "myApp";
    private FirebaseAuth mConnexion;
    private EditText mMail;
    private EditText mPassword;
    private Button mButtonOk;
    private Button offLine;
    Button mButtonCreate;


    ArrayList<String> arrayListCurrency = new ArrayList<String>();
    ArrayList<String> arrayListRate = new ArrayList<>();
    // hashMaps
    private HashMap<String, String> myHashEuro = new HashMap<>();


    /**
     * @param savedInstanceState
     */
    @SuppressLint({"MissingInflatedId", "WrongThread"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "app created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register_activity);

        autorizationApp();


        // initialization firebase auth
        mConnexion = FirebaseAuth.getInstance();
        mMail = (EditText) findViewById(R.id.mailEnter);
        mPassword = (EditText) findViewById(R.id.passwordEnter);
        mButtonOk = (Button) findViewById(R.id.validation);
        mButtonCreate = (Button) findViewById(R.id.toCreateActivity);
        offLine = (Button) findViewById(R.id.enterOffLineMode);

        offLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modeOfflineM();
            }
        });

        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn();
            }
        });

        mButtonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private boolean isConnected() {
        boolean connected = false;
        try{
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        }catch (Exception e){
            Log.d(TAG, "exception connexion");
        }
        return connected;
    }


    /**
     * This method will manage the autorisation of geo-localisation
     * This method will be called and the first installation of app or if the user disables
     * the geolocalisation autorisation in the parameters of his telephone
     */
    public void autorizationApp() {

        // manages localisation autorization
        if (ContextCompat.checkSelfPermission(LoginAndRegisterFirebase.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginAndRegisterFirebase.this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(LoginAndRegisterFirebase.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(LoginAndRegisterFirebase.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    /**
     * login method
     * check identification is correct and fields not empty
     */
    private void logIn() {


        // get info on fields
        String email = mMail.getText().toString();
        String password = mPassword.getText().toString();

        // test if editText are empty
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "please enter mail and password", Toast.LENGTH_SHORT).show();
            return;
        }


        mConnexion.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){ // if connexion is correct
                    changeToCurrenceActivity();
                }
            }
        });
    }

    private void modeOfflineM(){
        Intent modeOffLine = new Intent(this, ConvertionOffLine.class);
        startActivity(modeOffLine);

    }

    /**
     *
     */
    // switch to currency conversion
    private void changeToCurrenceActivity() {
        Intent goToCurrency = new Intent(this, ConversionMainActivity.class);
        goToCurrency.putExtra("currency init", arrayListCurrency);
        goToCurrency.putExtra("rate init", arrayListRate);
        goToCurrency.putExtra("hash map", myHashEuro);
        startActivity(goToCurrency);
        finish();
    }

    /**
     *
     */
    // will create and account and information will be stored authentification menu of firebase
    private void createAccount() {
        String email = mMail.getText().toString();
        String password = mPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()){ // if fields are not filled in
            Toast.makeText(this, "not possible to create account", Toast.LENGTH_LONG).show();
            return;
        }

        // create user and put in firebase
        mConnexion.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) { // add info in firebase
                if(task.isSuccessful()){
                    UserID newUser = new UserID(email, password);
                    FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance()
                                    .getCurrentUser().getUid()).setValue(newUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) { // once it was saved in database of firebase
                                    changeToCurrenceActivity();
                                }
                            });
                } else { // problem in firebase
                    Toast.makeText(LoginAndRegisterFirebase.this, "problem in authentification", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // LIFE CYCLE OF APP :)
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "App started");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "App paused");
    }

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
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "activity stoped");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "activity restarted");
    }
}