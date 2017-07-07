package com.floow.josueherrero.floowapp.ui.root;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.floow.josueherrero.floowapp.R;
import com.floow.josueherrero.floowapp.persistence.PathsRepositoryImpl;
import com.floow.josueherrero.floowapp.ui.base.FloowActivity;
import com.floow.josueherrero.floowapp.ui.list.ListFragment;
import com.floow.josueherrero.floowapp.ui.list.items.Path;
import com.floow.josueherrero.floowapp.utils.StringProviderImpl;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * Created by JosuéManuel on 25/06/2017.
 *
 * This is the main activity for our app. It manages the permission and location requests
 */
public final class LocationActivity extends FloowActivity<LocationActivityPresenter>
        implements LocationActivityPresenter.View, ListFragment.Callback, OnMapReadyCallback {

    private static final int ZOOM = 16;
    private static final String TAG = LocationActivity.class.getSimpleName();
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 800;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    @BindView(R.id.main_coordinator) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.location_tab) TabLayout tabLayout;
    @BindView(R.id.bar_pager) ViewPager viewPager;

    private BarLocationPagerAdapter pagerAdapter;
    private LocationActivityPresenterImpl presenter;
    private GoogleMap map;
    private MenuItem startTrackingMenuItem;
    private MenuItem stopTrackingMenuItem;
    private MenuItem recordMenuItem;
    private MenuItem stopRecordingMenuItem;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;

    /**
     * Views initialization, request location
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.title_activity_main));
        if(this.viewPager != null
                && this.tabLayout != null) {

            pagerAdapter = new BarLocationPagerAdapter(getSupportFragmentManager());
            this.viewPager.setAdapter(pagerAdapter);
            this.tabLayout.setupWithViewPager(this.viewPager);
            viewPager.addOnPageChangeListener(new ViewPagerListener() {

                @Override
                public void onPageSelected(int position) {
                    if (position == 1){
                        appBarLayout.setExpanded(false);
                    }
                }

            });
        }

        ((SupportMapFragment) pagerAdapter.getItem(1)).getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    /**
     * Checks if the permissions have been granted
     */
    @Override
    public void onResume() {
        if (!checkWriteExternalStoragePermission()) {
            requestPermission(getString(R.string.storage_permission_message),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!checkNetworkStatePermission()) {
            requestPermission(getString(R.string.network_state_permission),
                    Manifest.permission.ACCESS_NETWORK_STATE);
        } else {
            if (!checkNetwork()) {
                showMessage(getString(R.string.network_request));
            }
        }
        super.onResume();
    }

    /**
     * When google map is ready this is triggered and the event is passed to the presenter
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        presenter.onMapReady();
    }

    @Override
    public int getActivityLayout() {
        return R.layout.activity_bar_location;
    }

    @Override
    protected LocationActivityPresenter getPresenter() {
        if (presenter == null) {
            presenter = new LocationActivityPresenterImpl(new PathsRepositoryImpl(),new StringProviderImpl(this));
        }
        return this.presenter;
    }

    /**
     * The user has selected a path of the list to print on the map.
     * The date is needed for saving the current recording path
     */
    @Override
    public void onPrintPath(final int position) {
        final Calendar calendar = Calendar.getInstance();
        presenter.onPrintPath(position, calendar.getTime().getTime());
        viewPager.setCurrentItem(1,true);
    }

    /**
     * This event is triggered by the list fragment when it is ready to work
     */
    @Override
    public void onListCreated() {
        presenter.onListLoaded();
    }

    /**
     * This updates the map screen with the current trancking and recording paths
     */
    @Override
    public void showMapPaths(
            final List<LatLng> trackingPath,
            final List<LatLng> recordingPath) {

        if (map != null) {
            map.clear();
            if (!trackingPath.isEmpty()) {
                final PolylineOptions polylineOptions = new PolylineOptions();
                for (LatLng position : trackingPath) {
                    polylineOptions.add(position);
                }

                polylineOptions.width(5).color(Color.BLUE);

                map.addPolyline(polylineOptions);
            }

            if (!recordingPath.isEmpty()) {
                final PolylineOptions polylineOptions = new PolylineOptions();
                for (LatLng position : recordingPath) {
                    polylineOptions.add(position);
                }

                polylineOptions.width(5).color(Color.RED);

                map.addPolyline(polylineOptions);
            }
        }
    }

    /**
     * This updates the current user location mark on the map screen
     */
    @Override
    public void showLocationMark(final LatLng latLng) {
        if (map != null) {
            if (map.getCameraPosition().zoom < ZOOM) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 200));
                map.animateCamera(CameraUpdateFactory.zoomTo(ZOOM), 2000, null);
            }
            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.user_position_title))
                    .snippet(getString(R.string.user_position_description)));
        }
    }

    @Override
    public void showStopRecordingMenu() {
        if (recordMenuItem != null
                && stopRecordingMenuItem != null) {

            recordMenuItem.setVisible(false);
            stopRecordingMenuItem.setVisible(true);
        }
    }

    @Override
    public void showRecordMenu() {
        if (recordMenuItem != null
                && stopRecordingMenuItem != null) {

            recordMenuItem.setVisible(true);
            stopRecordingMenuItem.setVisible(false);
        }
    }

    @Override
    public void showTrackingMenu() {
        if (startTrackingMenuItem != null
                && stopTrackingMenuItem != null) {

            startTrackingMenuItem.setVisible(true);
            stopTrackingMenuItem.setVisible(false);
        }
    }

    @Override
    public void showStopTrackingMenu() {
        if (startTrackingMenuItem != null
                && stopTrackingMenuItem != null) {

            startTrackingMenuItem.setVisible(false);
            stopTrackingMenuItem.setVisible(true);
        }
    }

    /**
     * This updates the path list screen with the current paths
     */
    @Override
    public void showPathList(final List<Path> barListDataItems) {
        ((ListFragment)pagerAdapter.getItem(0)).setBarListItems(barListDataItems);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.record_menu, menu);
        startTrackingMenuItem = menu.findItem(R.id.start_tracking_menu_item);
        stopTrackingMenuItem = menu.findItem(R.id.stop_tracking_menu_item);
        recordMenuItem = menu.findItem(R.id.start_recording_menu_item);
        stopRecordingMenuItem = menu.findItem(R.id.stop_recording_menu_item);
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                presenter.initMenu();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.start_recording_menu_item) {
            if (checkFineLocationPermission()) {
                final Calendar calendar = Calendar.getInstance();
                final Date date = calendar.getTime();
                presenter.startRecording(date.getTime());
                viewPager.setCurrentItem(1, true);
            } else {
                requestPermission(getString(R.string.fine_location_permission_message), Manifest.permission.ACCESS_FINE_LOCATION);
            }
            return true;
        }

        if (id == R.id.stop_recording_menu_item) {
            final Calendar calendar = Calendar.getInstance();
            final Date date = calendar.getTime();
            presenter.stopRecording(date.getTime());
            return true;
        }

        if (id == R.id.start_tracking_menu_item) {
            if (checkFineLocationPermission()) {
                final Calendar calendar = Calendar.getInstance();
                final Date date = calendar.getTime();
                presenter.startTracking(date.getTime());
                viewPager.setCurrentItem(1,true);
            } else {
                requestPermission(getString(R.string.fine_location_permission_message), Manifest.permission.ACCESS_FINE_LOCATION);
            }
            return true;
        }

        if (id == R.id.stop_tracking_menu_item) {
            presenter.stopTracking();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This is the result event of the location settings user request
     */
    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        presenter.onResetLocationManagement();
                        break;
                }
                break;
        }
    }

    /**
     * This method triggers the location updates
     */
    @Override
    public void fetchLocation() {
        startLocationUpdates();
    }

    /**
     * This method stops the location updates
     */
    @Override
    public void stopLocationFetch() {
        stopLocationUpdates();
    }

    public void showMessageOKCancel(
            final String message,
            final DialogInterface.OnClickListener okListener) {

        new AlertDialog.Builder(LocationActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.positive_button), okListener)
                .setNegativeButton(getString(R.string.negative_button), null)
                .create()
                .show();
    }

    /**
     * This method triggers the location updates. First it checks if the location settings are correct,
     * it requests some user action if not.
     */
    private void startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(final LocationSettingsResponse locationSettingsResponse) {
                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        final int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    final ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                    resolvableApiException.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Log.e(TAG, "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.");
                        }
                    }
                });
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * This method creates the location request. It sets here the location updates configuration.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);
    }

    /**
     * This creates the callback to receive the location updates and pass the to the presenter.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                super.onLocationResult(locationResult);
                presenter.onLocationUpdated(locationResult.getLastLocation());
            }
        };
    }

    /**
     * This method creates the google settings request to use for checking the current location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * This is the method for requesting a permission.
     */
    private void requestPermission(
            final String message,
            final String permission) {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(LocationActivity.this,
                permission);
        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(LocationActivity.this,
                    permission)) {
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(LocationActivity.this,
                                        new String[] {permission},
                                        REQUEST_PERMISSIONS_REQUEST_CODE);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(LocationActivity.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
            return;
        }
    }

    @Override
    public boolean checkWriteExternalStoragePermission() {
        final int writeExternalStoragePermissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return writeExternalStoragePermissionState == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkFineLocationPermission() {
        final int fineLocationPermissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return fineLocationPermissionState == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkNetworkStatePermission() {
        final int fineLocationPermissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE);
        return fineLocationPermissionState == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkNetwork() {
        final ConnectivityManager connectivityManager =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void showMessage(final String message) {
        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        final View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        snackbar.show();
    }

    private final class BarLocationPagerAdapter extends FragmentPagerAdapter {

        final List<Fragment> fragmentList = new ArrayList<>();

        BarLocationPagerAdapter(final FragmentManager fragmentManager) {
            super(fragmentManager);
            if (fragmentManager.getFragments()!=null
                    &&!fragmentManager.getFragments().isEmpty()) {

                fragmentList.add(fragmentManager.getFragments().get(1));
                fragmentList.add(fragmentManager.getFragments().get(2));
            } else {
                fragmentList.add(ListFragment.newInstance());
                fragmentList.add(SupportMapFragment.newInstance());
            }
        }

        public Fragment getItem(final int position) {
            return fragmentList.get(position);
        }

        public int getCount() {
            return 2;
        }

        public CharSequence getPageTitle(final int position) {
            return position == 0? getString(R.string.bar_list_screen_title): getString(R.string.bar_map_screen_title);
        }

    }

}