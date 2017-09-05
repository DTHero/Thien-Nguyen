package com.example.internntthien.accountfacebook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.notifications.NotificationsManager;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;

public class MainActivity extends FragmentActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Button bt;
    LocationManager locationManager;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext()); //Requires to use facebook properties in an app
        callbackManager = CallbackManager.Factory.create();
//        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        bt = (Button) findViewById(R.id.bt);

        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if(!checkLocation())
                            LoginManager.getInstance().logOut();
                        else {
                            Toast.makeText(MainActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancel() {

                        Toast.makeText(MainActivity.this,"Login attempt canceled.", Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onError(FacebookException e) {
                        Toast.makeText(MainActivity.this,"Login attempt failed.", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(MainActivity.this,MainPage.class);
//                        startActivity(intent);
                    }
                });
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this
                        , Arrays.asList("public_profile", "user_friends"));
                LoginManager.getInstance().logInWithPublishPermissions(MainActivity.this
                        , Arrays.asList("publish_actions"));
            }
            });

//        loginButton = (LoginButton) findViewById(R.id.login_button);
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                if(!checkLocation())
//                    LoginManager.getInstance().logOut();
//                else {
//                    Toast.makeText(MainActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
//                    startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onCancel() {
//                Toast.makeText(MainActivity.this,"Login attempt canceled.", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onError(FacebookException e) {
//                Toast.makeText(MainActivity.this,"Login attempt failed.", Toast.LENGTH_LONG).show();
////                Intent intent = new Intent(MainActivity.this,MainPage.class);
////                startActivity(intent);
//            }
//        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**Check location enabled**/
    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }   //show notice

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }   //check provider location on/off

}