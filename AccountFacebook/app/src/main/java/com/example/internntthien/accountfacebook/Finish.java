package com.example.internntthien.accountfacebook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class Finish extends AppCompatActivity {

    private FirebaseDatabase database;
    private AccessToken token;
    private String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext()); //Requires to use facebook properties in an app
        database = FirebaseDatabase.getInstance();
        token = AccessToken.getCurrentAccessToken();
        deviceToken = FirebaseInstanceId.getInstance().getToken();


        removeAllSharedLocation();
        removeLastLocation("Coordinates");
        removeLastLocation(deviceToken);
//        database.getReference(token.getUserId()).removeValue();
        LoginManager.getInstance().logOut();
        changeStatus("Offline");
        finish();


    }

    public void removeLastLocation(String reference){

        Query getRef = database.getReference(reference).orderByChild("userID").equalTo(token.getUserId());

        getRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Finish", "onCancelled", databaseError.toException());
            }
        });
    }

    public void removeAllSharedLocation(){
        database.getReference(token.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Coordinates coordinate = snapshot.getValue(Coordinates.class);
                    removeLastLocation(coordinate.getDeviceToken());
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Finish.this, "Failed to read value.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void changeStatus(final String stt){
        Query getRef = database.getReference("Registered User").orderByChild("userID").equalTo(token.getUserId());
        getRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().child("status").setValue(stt);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Finish", "onCancelled", databaseError.toException());
            }
        });
    }
}
