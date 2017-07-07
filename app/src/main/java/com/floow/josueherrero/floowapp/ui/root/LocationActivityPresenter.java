package com.floow.josueherrero.floowapp.ui.root;

import android.location.Location;

import com.floow.josueherrero.floowapp.ui.base.FloowActivityPresenter;
import com.floow.josueherrero.floowapp.ui.list.items.Path;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by JosuéManuel on 25/06/2017.
 */

public interface LocationActivityPresenter extends FloowActivityPresenter<LocationActivityPresenter.View> {

    void onMapReady();

    void onPrintPath(
            int position,
            long time);

    void onListLoaded();

    void startTracking(long time);

    void startRecording(long time);

    void stopRecording(long time);

    void stopTracking();

    void initMenu();

    void onResetLocationManagement();

    void onLocationUpdated(final Location location);

    interface View  extends FloowActivityPresenter.FloowView {

        void fetchLocation();

        void stopLocationFetch();

        boolean checkWriteExternalStoragePermission();

        void showPathList(List<Path> barListDataItems);

        void showMapPaths(
                List<LatLng> trackingPath,
                List<LatLng> recordingPath);

        void showLocationMark(LatLng latLng);

        void showStopRecordingMenu();

        void showRecordMenu();

        void showTrackingMenu();

        void showStopTrackingMenu();

    }

}