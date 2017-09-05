package com.example.internntthien.accountfacebook;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , OnMapReadyCallback, LocationListener {

    public static final String LATITUDE = "com.example.internntthien.accountfacebook.LATITUDE";
    public static final String LONGITUDE = "com.example.internntthien.accountfacebook.LONGITUDE";

    ArrayAdapter<String> adapter;
    private GoogleMap mark;
    private LatLngBounds.Builder bounds;
    double longitude, latitude;
    protected LocationManager locationManager;
    Location location;
    private FirebaseDatabase database;
    private AccessToken token;
    private Profile profile;
    private String deviceToken;
    TextView username, status;
    ProfilePictureView profileView, dialogView;
    ListView user_list;
    private ArrayList<String> list;
    private boolean registered;
    AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_maps);
        mapFragment.getMapAsync(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        database = FirebaseDatabase.getInstance();
        token = AccessToken.getCurrentAccessToken();
        profile = Profile.getCurrentProfile();
        deviceToken = FirebaseInstanceId.getInstance().getToken();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


            registerDevice();
            changeStatus("Online");
            pushData("Coordinates");
            pushData(deviceToken);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        username = (TextView) headerView.findViewById(R.id.user_name);
        profileView = (ProfilePictureView) headerView.findViewById(R.id.imageView);
        status = (TextView) headerView.findViewById(R.id.status);
        user_list = (ListView) findViewById(R.id.user_list);

        username.setText(profile.getName());
        profileView.setProfileId(token.getUserId());
        status.setText(token.getUserId());

        /**SearchView**/
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(Main2Activity.this, android.R.layout.simple_list_item_1, list);
        user_list.setAdapter(adapter);
        loadData();
        user_list.setTextFilterEnabled(true);
        user_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                database.getReference("Registered User").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            final Coordinates coordinate = snapshot.getValue(Coordinates.class);
                                if (list.get(position).equals(coordinate.getName()
                                        + "\nUser ID: "
                                        + coordinate.getUserID())) {
                                        dialog = new AlertDialog.Builder(Main2Activity.this);
//                                LayoutInflater inflater = getLayoutInflater();
//                                View dialogLayout = inflater.inflate(R.layout.image_dialog, null);
                                    dialog.setTitle("Friend Information!")
//                                .setView(dialogLayout)
                                            .setMessage("User name: " + coordinate.getName()
                                                    + "\nUser ID: " + coordinate.getUserID()
                                                    + "\nStatus: " + coordinate.getStatus())
                                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                                }
                                            });
                                    dialog.show();
                                }

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Toast.makeText(Main2Activity.this, "Failed to read value.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.icon_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                user_list.setVisibility(View.VISIBLE);
                adapter.getFilter().filter(s);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                user_list.setVisibility(View.GONE);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {

//        } else
        if (id == R.id.nav_send) {
            Intent intent = new Intent(Main2Activity.this, Invite.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            removeLastLocation("Coordinates");
            removeLastLocation(deviceToken);
//            database.getReference(token.getUserId()).removeValue();
            Intent intent = new Intent(Main2Activity.this, ShareLocation.class);
            intent.putExtra(LATITUDE, getLocation().getLatitude());
            intent.putExtra(LONGITUDE, getLocation().getLongitude());
            startActivity(intent);
        }
        else if (id == R.id.nav_exit) {
            Intent intent = new Intent(getApplicationContext(), Finish.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        // Else if (id == R.id.nav_log_out) {
//            LoginManager.getInstance().logOut();
////            Intent intent = new Intent(getApplicationContext(), Finish.class);
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////            startActivity(intent);
//        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Necessary methods
     **/

    public Location getLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (locationManager != null) {
                if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationManager.removeUpdates(this);
                }
            }
        }
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER
                    , 0, 0, this);
            Log.d("GPS Enabled", "GPS Enabled");

            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                    , 0, 0, this);
            Log.d("Network", "Network");
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                location.getLongitude();
                location.getLatitude();
            }
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mark = googleMap;
        bounds = new LatLngBounds.Builder();
        LatLng cur_loc = new LatLng(getLocation().getLatitude(),
                getLocation().getLongitude());
        mark.addMarker(new MarkerOptions().position(cur_loc)
                .title("Your location!"));
        database.getReference(deviceToken).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mark.clear();
                LatLng cur_loc = new LatLng(getLocation().getLatitude(),
                        getLocation().getLongitude());
                mark.addMarker(new MarkerOptions().position(cur_loc)
                        .title("Your location!"));
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Coordinates coordinate = snapshot.getValue(Coordinates.class);
                         cur_loc = new LatLng(coordinate.getLatitude(),
                                coordinate.getLongitude());
                        if (!token.getUserId().equals(coordinate.getUserID())) {
                            mark.addMarker(new MarkerOptions().position(cur_loc)
                                    .title(coordinate.getName() + "'s location"
                                    +"\nUser ID: "+coordinate.getUserID()))
                                    .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.person_marker));
                        }
                        bounds.include(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()));
                }
                mark.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Main2Activity.this, "Failed to read value.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeLastLocation(String reference) {
        Query getRef = database.getReference(reference).orderByChild("userID").equalTo(token.getUserId());
        getRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Finish", "onCancelled", databaseError.toException());
            }
        });
    }

    private void loadData() {
        database.getReference("Registered User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Coordinates coordinate = snapshot.getValue(Coordinates.class);
                    if (!coordinate.getUserID().equals(token.getUserId())) {
                        list.add(coordinate.getName()
                                + "\nUser ID: "
                                + coordinate.getUserID());
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Main2Activity.this, "Failed to read value.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    } /**User to import registered list into Search **/

    private void pushData(String reference) {
        Coordinates coordinate = new Coordinates(
                token.getUserId(),
                getLocation().getLatitude(),
                getLocation().getLongitude(),
                profile.getName(),
                deviceToken,
                "Online");
        database.getReference(reference).push().setValue(coordinate);
    }

    private void registerDevice(){
        registered = false;
        database.getReference("Registered User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Coordinates coordinate = snapshot.getValue(Coordinates.class);
                    if(deviceToken.equals(coordinate.getDeviceToken())) {
                        registered = true;
                        break;
                    }
                }
                if(!registered) pushData("Registered User");
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Main2Activity.this, "Failed to read value.",
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
                Log.e("Main2Activity", "onCancelled", databaseError.toException());
            }
        });
    }
}
