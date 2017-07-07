package com.floow.josueherrero.floowapp.ui.root;

import android.location.Location;

import com.floow.josueherrero.floowapp.R;
import com.floow.josueherrero.floowapp.persistence.PathsRepository;
import com.floow.josueherrero.floowapp.ui.base.FloowActivityPresenterImpl;
import com.floow.josueherrero.floowapp.ui.list.items.Path;
import com.floow.josueherrero.floowapp.utils.StringProvider;
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JosuéManuel on 25/06/2017.
 *
 * This is the presenter for the location screen
 */

public final class LocationActivityPresenterImpl extends FloowActivityPresenterImpl<LocationActivityPresenterImpl.State, LocationActivityPresenter.View>
        implements LocationActivityPresenter {

    private final PathsRepository pathsRepository;
    private final StringProvider stringProvider;

    public LocationActivityPresenterImpl(
            final PathsRepository pathsRepository,
            final StringProvider stringProvider) {

        this.pathsRepository = pathsRepository;
        this.stringProvider = stringProvider;
    }

    protected LocationActivityPresenterImpl.State newStateInstance() {
        return new State();
    }

    /**
     * This method updates the map screen, load the path list from a file and update it in the list
     * screen and initialize the toolbar menu.
     */
    @Override
    public void onViewAttached() {
        if (!state.trackingPath.getLatLngList().isEmpty()
                || !state.recordingPath.getLatLngList().isEmpty()) {

            if (hasView()) {
                updateMap();
            }
        }

        if (state.pathsList!=null
                && state.pathsList.isEmpty()) {

            if (hasView()
                    && getView().checkWriteExternalStoragePermission()) {

                if (pathsRepository.exists()) {
                    state.pathsList = pathsRepository.loadPaths();
                }

            }
        }

        if (state.pathsList != null
                && !state.pathsList.isEmpty()) {

            if (hasView()) {
                getView().showPathList(state.pathsList);
            }
        }

        initMenu();
    }

    /**
     * This method stops the location updates if the user is not recording a path
     */
    @Override
    public void beforeViewDetached() {
        if (!state.recording) {
            if (hasView()) {
                getView().stopLocationFetch();
            }
        }
    }

    /**
     * This is the method triggered when a location update is received. It updates the current path with it
     */
    @Override
    public void onLocationUpdated(final Location location) {
        Path currentPath;
        if (state.recording) {
            currentPath = state.recordingPath;
        } else {
            currentPath = state.trackingPath;
        }
        if (currentPath.getLatLngList().isEmpty()
                || (!currentPath.getLatLngList().isEmpty()
                            && (location.getLatitude() != currentPath.getLatLngList().get(currentPath.getLatLngList().size() - 1).latitude
                                    || location.getLongitude() != currentPath.getLatLngList().get(currentPath.getLatLngList().size() - 1).longitude))) {

            currentPath.getLatLngList().add(new LatLng(location.getLatitude(), location.getLongitude()));
            if (hasView()) {
                updateMap();
            }
        }
    }

    /**
     * This method is triggered when the google map is ready for work, it updates the map with the current
     * recording and tracking paths.
     */
    @Override
    public void onMapReady() {
        if (hasView()
                && (!state.trackingPath.getLatLngList().isEmpty() || !state.recordingPath.getLatLngList().isEmpty())) {

            updateMap();
        }
    }

    /**
     * This method clears the map screen and prints the selected stored path on it.
     * It saves the current recording before the clearing.
     */
    @Override
    public void onPrintPath(
            final int position,
            final long time) {

        state.printedPath = true;
        if (!state.recordingPath.getLatLngList().isEmpty()) {
            savePath(time);
        }
        final List<LatLng> latLngList = new ArrayList<>();
        for (LatLng latLng : state.pathsList.get(position).getLatLngList()) {
            latLngList.add(new LatLng(latLng.latitude, latLng.longitude));
        }
        state.trackingPath = new Path(state.pathsList.get(position).getName(), latLngList,
                state.pathsList.get(position).getStartTime(), state.pathsList.get(position).getEndTime());
        if (hasView()) {
            getView().stopLocationFetch();
            getView().showTrackingMenu();
            getView().showRecordMenu();
            updateMap();
            getView().showPathList(state.pathsList);
        }
    }

    /**
     * This is triggered when the list is ready for work
     */
    @Override
    public void onListLoaded() {
        if (hasView()) {
            getView().showPathList(state.pathsList);
        }
    }

    /**
     * This saves the current recording if needed and starts/continues a standard tracking.
     * If a path from the list was printed it clears it before.
     */
    @Override
    public void startTracking(final long time) {
        if (state.printedPath) {
            state.trackingPath.getLatLngList().clear();
            state.printedPath = false;
        }
        if (state.recording) {
            savePath(time);
            if (hasView()) {
                getView().showPathList(state.pathsList);
                getView().showRecordMenu();
            }
        } else {
            if (hasView()) {
                getView().fetchLocation();
            }
        }
        state.tracking = true;
        if (hasView()) {
            getView().showStopTrackingMenu();
        }
    }

    @Override
    public void stopTracking() {
        state.tracking = false;
        if (hasView()) {
            getView().showTrackingMenu();
            getView().stopLocationFetch();
        }
    }

    /**
     * This starts a new recording path.
     * If a path from the list was printed it clears it before.
     */
    @Override
    public void startRecording(final long time) {
        if (state.printedPath) {
            state.trackingPath.getLatLngList().clear();
            state.printedPath = false;
        }
        state.recordingPath.getLatLngList().clear();
        state.recordingPath.setStartTime(time);
        state.recording = true;
        state.tracking = false;
        if (hasView()) {
            getView().fetchLocation();
            getView().showStopRecordingMenu();
            getView().showTrackingMenu();
            updateMap();
        }
    }

    @Override
    public void stopRecording(final long time) {
        savePath(time);
        if (hasView()) {
            getView().showPathList(state.pathsList);
            getView().showRecordMenu();
            getView().stopLocationFetch();
        }
    }

    @Override
    public void initMenu() {
        if (hasView()) {
            if (state.recording) {
                getView().showStopRecordingMenu();
            } else {
                getView().showRecordMenu();
            }
            if (state.tracking) {
                getView().showStopTrackingMenu();
            } else {
                getView().showTrackingMenu();
            }
        }
    }

    /**
     * This reset the mode to the initialization point.
     * It is triggered when the location settings are not correct and the user cancels the dialog.
     */
    @Override
    public void onResetLocationManagement() {
        state.trackingPath = new Path("", new ArrayList<LatLng>(), 0, 0);
        state.recordingPath = new Path("", new ArrayList<LatLng>(), 0, 0);
        state.tracking = false;
        state.recording = false;
        state.printedPath = false;
        if (hasView()) {
            getView().showRecordMenu();
            getView().showTrackingMenu();
        }
    }

    /**
     * This updates the map screen with the current tracking and recording paths.
     */
    private void updateMap() {
        getView().showMapPaths(state.trackingPath.getLatLngList(), state.recordingPath.getLatLngList());
        if (state.recording) {
            if (!state.recordingPath.getLatLngList().isEmpty()) {
                getView().showLocationMark(state.recordingPath.getLatLngList().get(state.recordingPath.getLatLngList().size()-1));
            }
        } else {
            if (!state.trackingPath.getLatLngList().isEmpty()) {
                getView().showLocationMark(state.trackingPath.getLatLngList().get(state.trackingPath.getLatLngList().size()-1));
            }
        }
    }

    /**
     * This saves the current recording path to disk, It updates the path list and It updates the
     * tracking path with the recorded one.
     */
    private void savePath(final long time) {
        state.recordingPath.setEndTime(time);
        state.recordingPath.setName(stringProvider.getString(R.string.path_name) + " " + Integer.toString(state.pathsList.size() + 1));
        final List<LatLng> latLngList = new ArrayList<>();
        for (LatLng latLng : state.recordingPath.getLatLngList()) {
            latLngList.add(new LatLng(latLng.latitude, latLng.longitude));
        }
        for (LatLng latLng : state.recordingPath.getLatLngList()) {
            state.trackingPath.getLatLngList().add(new LatLng(latLng.latitude, latLng.longitude));
        }
        state.pathsList.add(new Path(state.recordingPath.getName(), latLngList,
                state.recordingPath.getStartTime(), state.recordingPath.getEndTime()));
        state.recordingPath.getLatLngList().clear();
        pathsRepository.savePaths(state.pathsList);
        state.recording = false;
    }

    /**
     * This is the presenter state witch will be stored in the bundle for its persistence on configuration
     * changes.
     */
    @Parcel
    public static class State implements FloowActivityPresenterImpl.State {

        boolean tracking = false;
        boolean recording = false;
        boolean printedPath = false;
        List<Path> pathsList = new ArrayList<>();
        Path trackingPath;
        Path recordingPath;

        State() {
            trackingPath = new Path("", new ArrayList<LatLng>(), 0, 0);
            recordingPath = new Path("", new ArrayList<LatLng>(), 0, 0);
        }

    }

}