package com.example.internntthien.accountfacebook;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShareLocation extends AppCompatActivity {

    TextView tv;
    Button bt_share;
    ListView available_friends;
    private FirebaseDatabase database;
    private ArrayList<String> list, listDevice;
    private ArrayAdapter adapter;
    private AccessToken token;
    private String deviceToken;
    private Profile profile;
    double latitude = 0, longitude = 0;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

        database = FirebaseDatabase.getInstance();
        token = AccessToken.getCurrentAccessToken();
        profile = Profile.getCurrentProfile();
        deviceToken = FirebaseInstanceId.getInstance().getToken();

        tv = (TextView) findViewById(R.id.tv);
        bt_share = (Button) findViewById(R.id.bt_share);
        available_friends = (ListView) findViewById(R.id.available_list);

        listDevice = new ArrayList<String>();
        list = new ArrayList<String>();
        adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_multiple_choice,
                list);
        available_friends.setAdapter(adapter);

        database.getReference("Coordinates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listDevice.clear();
                list.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Coordinates coordinate = snapshot.getValue(Coordinates.class);
                        list.add(coordinate.getName()
                                + "\nUser ID: "
                                + coordinate.getUserID());
                        listDevice.add(coordinate.getDeviceToken());
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(ShareLocation.this, "Failed to read value.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = available_friends.getCheckedItemPositions();
                for(int i=0; i<list.size();i++){
                    if(checked.get(i)) {
                        sendNotification(listDevice.get(i));
                        pushData(listDevice.get(i));
                        Coordinates coordinate = new Coordinates(
                                "userID",
                                0,
                                0,
                                "name",
                                listDevice.get(i),
                                "status");
                        database.getReference(token.getUserId()).push().setValue(coordinate);
                    }
                }


            Intent intent = new Intent(ShareLocation.this, Main2Activity.class);
            startActivity(intent);
            Toast.makeText(ShareLocation.this,"Share successfully!", Toast.LENGTH_LONG).show();
        }

    });

    }


    private void pushData(String reference){
        Intent getLocation = getIntent();
        Coordinates coordinate = new Coordinates(
                token.getUserId(),
                getLocation.getDoubleExtra(Main2Activity.LATITUDE,latitude),
                getLocation.getDoubleExtra(Main2Activity.LONGITUDE,longitude),
                profile.getName(),
                deviceToken,
                "Online");
        database.getReference(reference).push().setValue(coordinate);

    }

    private void sendNotification(final String tokenID) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json= new JSONObject();
                    JSONObject dataJson= new JSONObject();
                    dataJson.put("body",profile.getName()+" want to share location with you.");
                    dataJson.put("title","New device location found!");
                    json.put("notification",dataJson);
                    json.putOpt("to",tokenID);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key=AIzaSyBJPOhBTbNjWqsRrUAbsry41xsZfgWrtMY")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                }catch (Exception e){
                    //Log.d(TAG,e+"");
                }
                return null;
            }
        }.execute();

    }

}
