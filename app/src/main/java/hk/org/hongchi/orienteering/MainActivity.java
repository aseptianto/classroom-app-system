package hk.org.hongchi.orienteering;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.keysolutions.ddpclient.android.DDPBroadcastReceiver;
import com.keysolutions.ddpclient.android.DDPStateSingleton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Map;

import hk.org.hongchi.orienteering.maps.Route;
import hk.org.hongchi.orienteering.models.Place;
import hk.org.hongchi.orienteering.utils.IntentIntegrator;
import hk.org.hongchi.orienteering.utils.IntentResult;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PlacesFragment.DirectionsClickListener, PlacesFragment.Callbacks {
    private LocationManager locationManager;
    private GoogleMap map;
    private Route route;

    private SlidingUpPanelLayout slidingLayout;
    private PlacesFragment placesFragment;
    private Button btnQuiz;

    private TextView txtStudentName;

    private DDPBroadcastReceiver ddpReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        placesFragment = (PlacesFragment) getFragmentManager().findFragmentById(R.id.directions);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
            }
        });

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);

        route = new Route();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,
//                1f, this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        txtStudentName = (TextView) headerView.findViewById(R.id.student_name);
    }

    @Override
    public void onResume() {
        super.onResume();

        ddpReceiver = new DDPBroadcastReceiver(DDPService.getInstance(), this) {
            @Override
            protected void onDDPConnect(DDPStateSingleton ddp) {
                super.onDDPConnect(ddp);

                String userId = ddp.getUserId();

                ddp.subscribe("users", new Object[]{userId});
                ddp.subscribe("sessions", new Object[]{userId});
                ddp.subscribe("questions", new Object[]{userId});
                ddp.subscribe("places", new Object[]{userId});
                ddp.subscribe("submissions", new Object[]{userId});
            }

            @Override
            protected void onSubscriptionUpdate(String changeType,
                                                String subscriptionName, String docId) {
                if (subscriptionName.equals("users")) {
                    updateUserInfo();
                }
            }
        };
        DDPService.getInstance().connectIfNeeded();    // start connection process if we're not connected
    }

    @Override
    public void onPause() {
        super.onPause();

        if (ddpReceiver != null) {
            LocalBroadcastManager.getInstance(this)
                    .unregisterReceiver(ddpReceiver);
            ddpReceiver = null;
        }
    }

    private void updateUserInfo() {
        Map<String, Object> userInfo = DDPService.getInstance().getUser();
        txtStudentName.setText((String) userInfo.get("name"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            Place current = DDPService.getInstance().getPlace(result.getContents());
            if (current != null) {
                placesFragment.unlockDestination(current);
            }
        }
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            DDPService.getInstance().logout();
            Intent i = new Intent(this, LoginActivity.class);

            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPlaceSelect(View trackItemView, Place track, int position) {
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public void onDestinationUpdate(Place current, Place dest) {
        map.clear();

        map.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition(current.toLatLng(), 16, 0, map.getCameraPosition().bearing)));

        map.addMarker(new MarkerOptions().position(current.toLatLng()));
        map.addMarker(new MarkerOptions().position(dest.toLatLng()));

        route.drawRoute(map, this, current.toLatLng(), dest.toLatLng(), false, Route.LANGUAGE_ENGLISH);
    }
}
