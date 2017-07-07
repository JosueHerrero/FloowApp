package com.floow.josueherrero.floowapp.ui.root;

import android.location.Location;

import com.floow.josueherrero.floowapp.persistence.PathsRepository;
import com.floow.josueherrero.floowapp.ui.list.items.Path;
import com.floow.josueherrero.floowapp.utils.StringProvider;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.truth.Truth;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.capture;

/**
 * Created by JosuéManuel on 05/07/2017.
 */

@RunWith(PowerMockRunner.class)
public class LocationActivityPresenterImplTest {
    
    private final static double LATITUDE_1_1 = 11d;
    private final static double LATITUDE_1_2 = 12d;
    private final static double LATITUDE_2_1 = 21d;
    private final static double LATITUDE_2_2 = 22d;
    private final static double LATITUDE_3_1 = 31d;
    private final static double LATITUDE_3_2 = 32d;
    private final static double LATITUDE_4_1 = 41d;
    private final static double LATITUDE_4_2 = 42d;

    private final static double LONGITUDE_1_1 = 11d;
    private final static double LONGITUDE_1_2 = 12d;
    private final static double LONGITUDE_2_1 = 21d;
    private final static double LONGITUDE_2_2 = 22d;
    private final static double LONGITUDE_3_1 = 31d;
    private final static double LONGITUDE_3_2 = 32d;
    private final static double LONGITUDE_4_1 = 41d;
    private final static double LONGITUDE_4_2 = 42d;

    private final static long STARTING_DATE_1 = 1;
    private final static long STARTING_DATE_2 = 2;
    private final static long STARTING_DATE_3 = 3;
    private final static long STARTING_DATE_4 = 4;
    private final static long END_DATE_1 = 11;
    private final static long END_DATE_2 = 22;
    private final static long END_DATE_3 = 33;
    private final static long END_DATE_4 = 44;

    private final static String PATH_NAME_1 = "first";
    private final static String PATH_NAME_2 = "second";
    private final static String PATH_NAME_3 = "third";
    private final static String PATH_NAME_4 = "forth";

    private LocationActivityPresenterImpl presenter;
    private LocationActivityPresenter.View view;
    private PathsRepository pathsRepository;
    private LocationActivityPresenterImpl.State state;
    private StringProvider stringProvider;

    @Before
    public void setUp() {
        view = PowerMock.createMock(LocationActivityPresenter.View.class);
        pathsRepository = PowerMock.createMock(PathsRepository.class);
        state = new LocationActivityPresenterImpl.State();
        stringProvider = PowerMock.createMock(StringProvider.class);
        presenter = new LocationActivityPresenterImpl(pathsRepository, stringProvider);
        presenter.setState(state);
        presenter.setView(view);
    }

    @After
    public void cleanUp() {
        PowerMock.verifyAll();
        PowerMock.resetAll();
    }

    @Test
    public void onViewAttached_NoData() {
        EasyMock.expect(pathsRepository.exists()).andReturn(true).once();

        EasyMock.expect(view.checkWriteExternalStoragePermission()).andReturn(true);

        final List<LatLng> latLngList1 = new ArrayList<>();
        latLngList1.add(new LatLng(LATITUDE_1_1, LONGITUDE_1_1));
        latLngList1.add(new LatLng(LATITUDE_1_2, LONGITUDE_1_2));

        final List<LatLng> latLngList2 = new ArrayList<>();
        latLngList2.add(new LatLng(LATITUDE_2_1, LONGITUDE_2_1));
        latLngList2.add(new LatLng(LATITUDE_2_2, LONGITUDE_2_2));

        final List<Path> pathList = new ArrayList<>();
        pathList.add(new Path(PATH_NAME_1, latLngList1, STARTING_DATE_1, END_DATE_1));
        pathList.add(new Path(PATH_NAME_2, latLngList2, STARTING_DATE_2, END_DATE_2));

        final Capture<List<Path>> pathListToCapture = Capture.newInstance();
        view.showPathList(capture(pathListToCapture));
        view.showRecordMenu();
        view.showTrackingMenu();

        EasyMock.expect(pathsRepository.loadPaths()).andReturn(pathList).once();

        PowerMock.replayAll();
        presenter.attach(view);

        final List<Path> capturedPathList = pathListToCapture.getValue();

        Truth.assertThat(capturedPathList.size() == 2).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().size() == 2).isTrue();
        Truth.assertThat(capturedPathList.get(0).getName()).isEqualTo(PATH_NAME_1);
        Truth.assertThat(capturedPathList.get(0).getStartTime() == STARTING_DATE_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getEndTime() == END_DATE_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(0).latitude == LATITUDE_1_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(0).longitude == LONGITUDE_1_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(1).latitude == LATITUDE_1_2).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(1).longitude == LONGITUDE_1_2).isTrue();

        Truth.assertThat(capturedPathList.get(1).getName()).isEqualTo(PATH_NAME_2);
        Truth.assertThat(capturedPathList.get(1).getStartTime() == STARTING_DATE_2).isTrue();
        Truth.assertThat(capturedPathList.get(1).getEndTime() == END_DATE_2).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(0).latitude == LATITUDE_2_1).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(0).longitude == LONGITUDE_2_1).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(1).latitude == LATITUDE_2_2).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(1).longitude == LONGITUDE_2_2).isTrue();
    }

    @Test
    public void onViewAttached_WithData() {
        final List<LatLng> latLngList1 = new ArrayList<>();
        latLngList1.add(new LatLng(LATITUDE_1_1, LONGITUDE_1_1));
        latLngList1.add(new LatLng(LATITUDE_1_2, LONGITUDE_1_2));

        final List<LatLng> latLngList2 = new ArrayList<>();
        latLngList2.add(new LatLng(LATITUDE_2_1, LONGITUDE_2_1));
        latLngList2.add(new LatLng(LATITUDE_2_2, LONGITUDE_2_2));

        final List<LatLng> latLngList3 = new ArrayList<>();
        latLngList3.add(new LatLng(LATITUDE_3_1, LONGITUDE_3_1));
        latLngList3.add(new LatLng(LATITUDE_3_2, LONGITUDE_3_2));

        final List<LatLng> latLngList4 = new ArrayList<>();
        latLngList4.add(new LatLng(LATITUDE_4_1, LONGITUDE_4_1));
        latLngList4.add(new LatLng(LATITUDE_4_2, LONGITUDE_4_2));

        state.pathsList.add(new Path(PATH_NAME_1, latLngList1, STARTING_DATE_1, END_DATE_1));
        state.pathsList.add(new Path(PATH_NAME_2, latLngList2, STARTING_DATE_2, END_DATE_2));

        state.recordingPath = new Path(PATH_NAME_3, latLngList3, STARTING_DATE_3, END_DATE_3);
        state.trackingPath = new Path(PATH_NAME_4, latLngList4, STARTING_DATE_4, END_DATE_4);

        state.recording = true;

        final Capture<List<LatLng>> trackedPathLocationsToCapture = Capture.newInstance();
        final Capture<List<LatLng>> recordedPathLocationsToCapture = Capture.newInstance();
        view.showMapPaths(capture(trackedPathLocationsToCapture), capture(recordedPathLocationsToCapture));

        final Capture<LatLng> locationMarkToCapture = Capture.newInstance();
        view.showLocationMark(capture(locationMarkToCapture));

        final Capture<List<Path>> pathListToCapture = Capture.newInstance();
        view.showPathList(capture(pathListToCapture));

        view.showStopRecordingMenu();
        view.showTrackingMenu();

        PowerMock.replayAll();

        presenter.attach(view);

        final List<LatLng> capturedTrackedPathLocationsList = trackedPathLocationsToCapture.getValue();
        Truth.assertThat(capturedTrackedPathLocationsList.size() == 2).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(0).latitude == LATITUDE_4_1).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(0).longitude == LONGITUDE_4_1).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(1).latitude == LATITUDE_4_2).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(1).longitude == LONGITUDE_4_2).isTrue();

        final List<LatLng> capturedRecordedPathList = recordedPathLocationsToCapture.getValue();
        Truth.assertThat(capturedRecordedPathList.size() == 2).isTrue();
        Truth.assertThat(capturedRecordedPathList.get(0).latitude == LATITUDE_3_1).isTrue();
        Truth.assertThat(capturedRecordedPathList.get(0).longitude == LONGITUDE_3_1).isTrue();
        Truth.assertThat(capturedRecordedPathList.get(1).latitude == LATITUDE_3_2).isTrue();
        Truth.assertThat(capturedRecordedPathList.get(1).longitude == LONGITUDE_3_2).isTrue();

        final LatLng capturedLocationMark = locationMarkToCapture.getValue();
        Truth.assertThat(capturedLocationMark.latitude == LATITUDE_3_2).isTrue();
        Truth.assertThat(capturedLocationMark.longitude == LONGITUDE_3_2).isTrue();

        final List<Path> capturedPathList = pathListToCapture.getValue();
        Truth.assertThat(capturedPathList.size() == 2).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().size() == 2).isTrue();
        Truth.assertThat(capturedPathList.get(0).getName()).isEqualTo(PATH_NAME_1);
        Truth.assertThat(capturedPathList.get(0).getStartTime() == STARTING_DATE_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getEndTime() == END_DATE_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(0).latitude == LATITUDE_1_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(0).longitude == LONGITUDE_1_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(1).latitude == LATITUDE_1_2).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(1).longitude == LONGITUDE_1_2).isTrue();

        Truth.assertThat(capturedPathList.get(1).getName()).isEqualTo(PATH_NAME_2);
        Truth.assertThat(capturedPathList.get(1).getStartTime() == STARTING_DATE_2).isTrue();
        Truth.assertThat(capturedPathList.get(1).getEndTime() == END_DATE_2).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(0).latitude == LATITUDE_2_1).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(0).longitude == LONGITUDE_2_1).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(1).latitude == LATITUDE_2_2).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(1).longitude == LONGITUDE_2_2).isTrue();
    }

    @Test
    public void beforeViewDetached_notRecording() {
        state.recording = false;
        view.stopLocationFetch();

        PowerMock.replayAll();
        presenter.beforeViewDetached();
    }

    @Test
    public void onLocationUpdated_recording() {
        final List<LatLng> latLngList1 = new ArrayList<>();
        latLngList1.add(new LatLng(LATITUDE_1_1, LONGITUDE_1_1));
        latLngList1.add(new LatLng(LATITUDE_1_2, LONGITUDE_1_2));
        state.recordingPath = new Path(PATH_NAME_1, latLngList1, STARTING_DATE_1, END_DATE_1);

        state.recording = true;
        final Location location = PowerMock.createMock(Location.class);
        EasyMock.expect(location.getLatitude()).andReturn(LATITUDE_3_1).times(2);
        EasyMock.expect(location.getLongitude()).andReturn(LONGITUDE_3_1).once();

        final List<LatLng> latLngList2 = new ArrayList<>();
        latLngList2.add(new LatLng(LATITUDE_2_1, LONGITUDE_2_1));
        latLngList2.add(new LatLng(LATITUDE_2_2, LONGITUDE_2_2));
        state.trackingPath = new Path(PATH_NAME_2, latLngList2, STARTING_DATE_2, END_DATE_2);

        final Capture<List<LatLng>> trackedPathLocationsToCapture = Capture.newInstance();
        final Capture<List<LatLng>> recordedPathLocationsToCapture = Capture.newInstance();
        view.showMapPaths(capture(trackedPathLocationsToCapture), capture(recordedPathLocationsToCapture));

        final Capture<LatLng> locationMarkToCapture = Capture.newInstance();
        view.showLocationMark(capture(locationMarkToCapture));

        PowerMock.replayAll();
        presenter.onLocationUpdated(location);

        Truth.assertThat(state.recordingPath.getLatLngList().get(2).latitude == LATITUDE_3_1).isTrue();
        Truth.assertThat(state.recordingPath.getLatLngList().get(2).longitude == LONGITUDE_3_1).isTrue();

        final List<LatLng> capturedTrackedPathLocationsList = trackedPathLocationsToCapture.getValue();
        Truth.assertThat(capturedTrackedPathLocationsList.size() == 2).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(0).latitude == LATITUDE_2_1).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(0).longitude == LONGITUDE_2_1).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(1).latitude == LATITUDE_2_2).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(1).longitude == LONGITUDE_2_2).isTrue();

        final List<LatLng> capturedRecordedPathList = recordedPathLocationsToCapture.getValue();
        Truth.assertThat(capturedRecordedPathList.size() == 3).isTrue();
        Truth.assertThat(capturedRecordedPathList.get(0).latitude == LATITUDE_1_1).isTrue();
        Truth.assertThat(capturedRecordedPathList.get(0).longitude == LONGITUDE_1_1).isTrue();
        Truth.assertThat(capturedRecordedPathList.get(1).latitude == LATITUDE_1_2).isTrue();
        Truth.assertThat(capturedRecordedPathList.get(1).longitude == LONGITUDE_1_2).isTrue();

        final LatLng capturedLocationMark = locationMarkToCapture.getValue();
        Truth.assertThat(capturedLocationMark.latitude == LATITUDE_3_1).isTrue();
        Truth.assertThat(capturedLocationMark.longitude == LONGITUDE_3_1).isTrue();
    }

    @Test
    public void onMapReady_recording() {
        final List<LatLng> latLngList1 = new ArrayList<>();
        latLngList1.add(new LatLng(LATITUDE_1_1, LONGITUDE_1_1));
        latLngList1.add(new LatLng(LATITUDE_1_2, LONGITUDE_1_2));
        state.recordingPath = new Path(PATH_NAME_1, latLngList1, STARTING_DATE_1, END_DATE_1);

        state.recording = true;

        final List<LatLng> latLngList2 = new ArrayList<>();
        latLngList2.add(new LatLng(LATITUDE_2_1, LONGITUDE_2_1));
        latLngList2.add(new LatLng(LATITUDE_2_2, LONGITUDE_2_2));
        state.trackingPath = new Path(PATH_NAME_2, latLngList2, STARTING_DATE_2, END_DATE_2);

        final Capture<List<LatLng>> trackedPathLocationsToCapture = Capture.newInstance();
        final Capture<List<LatLng>> recordedPathLocationsToCapture = Capture.newInstance();
        view.showMapPaths(capture(trackedPathLocationsToCapture), capture(recordedPathLocationsToCapture));

        final Capture<LatLng> locationMarkToCapture = Capture.newInstance();
        view.showLocationMark(capture(locationMarkToCapture));

        PowerMock.replayAll();

        presenter.onMapReady();

        final List<LatLng> capturedTrackedPathLocationsList = trackedPathLocationsToCapture.getValue();
        Truth.assertThat(capturedTrackedPathLocationsList.size() == 2).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(0).latitude == LATITUDE_2_1).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(0).longitude == LONGITUDE_2_1).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(1).latitude == LATITUDE_2_2).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(1).longitude == LONGITUDE_2_2).isTrue();

        final List<LatLng> capturedRecordedPathList = recordedPathLocationsToCapture.getValue();
        Truth.assertThat(capturedRecordedPathList.size() == 2).isTrue();
        Truth.assertThat(capturedRecordedPathList.get(0).latitude == LATITUDE_1_1).isTrue();
        Truth.assertThat(capturedRecordedPathList.get(0).longitude == LONGITUDE_1_1).isTrue();
        Truth.assertThat(capturedRecordedPathList.get(1).latitude == LATITUDE_1_2).isTrue();
        Truth.assertThat(capturedRecordedPathList.get(1).longitude == LONGITUDE_1_2).isTrue();

        final LatLng capturedLocationMark = locationMarkToCapture.getValue();
        Truth.assertThat(capturedLocationMark.latitude == LATITUDE_1_2).isTrue();
        Truth.assertThat(capturedLocationMark.longitude == LONGITUDE_1_2).isTrue();
    }

    @Test
    public void onPrintPath_default() {
        final List<LatLng> latLngList1 = new ArrayList<>();
        latLngList1.add(new LatLng(LATITUDE_1_1, LONGITUDE_1_1));
        latLngList1.add(new LatLng(LATITUDE_1_2, LONGITUDE_1_2));
        state.recordingPath = new Path(PATH_NAME_1, latLngList1, STARTING_DATE_1, END_DATE_1);

        final List<LatLng> latLngList2 = new ArrayList<>();
        latLngList2.add(new LatLng(LATITUDE_2_1, LONGITUDE_2_1));
        latLngList2.add(new LatLng(LATITUDE_2_2, LONGITUDE_2_2));
        state.trackingPath = new Path(PATH_NAME_2, latLngList2, STARTING_DATE_2, END_DATE_2);

        final List<LatLng> latLngList3 = new ArrayList<>();
        latLngList3.add(new LatLng(LATITUDE_3_1, LONGITUDE_3_1));
        latLngList3.add(new LatLng(LATITUDE_3_2, LONGITUDE_3_2));

        state.pathsList.add(new Path(PATH_NAME_3, latLngList3, STARTING_DATE_3, END_DATE_3));

        EasyMock.expect(stringProvider.getString(EasyMock.anyInt())).andReturn("").once();
        final Capture<List<Path>> savedPathListToCapture = Capture.newInstance();
        pathsRepository.savePaths(capture(savedPathListToCapture));
        view.stopLocationFetch();
        view.showTrackingMenu();
        view.showRecordMenu();

        final Capture<List<LatLng>> trackedPathLocationsToCapture = Capture.newInstance();
        final Capture<List<LatLng>> recordedPathLocationsToCapture = Capture.newInstance();
        view.showMapPaths(capture(trackedPathLocationsToCapture), capture(recordedPathLocationsToCapture));

        final Capture<LatLng> locationMarkToCapture = Capture.newInstance();
        view.showLocationMark(capture(locationMarkToCapture));

        final Capture<List<Path>> pathListToCapture = Capture.newInstance();
        view.showPathList(capture(pathListToCapture));

        PowerMock.replayAll();

        presenter.onPrintPath(0,END_DATE_1);

        Truth.assertThat(state.printedPath).isTrue();

        final List<LatLng> capturedTrackedPathLocationsList = trackedPathLocationsToCapture.getValue();
        Truth.assertThat(capturedTrackedPathLocationsList.size() == 2).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(0).latitude == LATITUDE_3_1).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(0).longitude == LONGITUDE_3_1).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(1).latitude == LATITUDE_3_2).isTrue();
        Truth.assertThat(capturedTrackedPathLocationsList.get(1).longitude == LONGITUDE_3_2).isTrue();

        final List<LatLng> capturedRecordedPathList = recordedPathLocationsToCapture.getValue();
        Truth.assertThat(capturedRecordedPathList.size() == 0).isTrue();

        final LatLng capturedLocationMark = locationMarkToCapture.getValue();
        Truth.assertThat(capturedLocationMark.latitude == LATITUDE_3_2).isTrue();
        Truth.assertThat(capturedLocationMark.longitude == LONGITUDE_3_2).isTrue();

        final List<Path> capturedPathList = pathListToCapture.getValue();

        Truth.assertThat(capturedPathList.size() == 2).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().size() == 2).isTrue();
        Truth.assertThat(capturedPathList.get(1).getName()).isEqualTo(" 2");
        Truth.assertThat(capturedPathList.get(1).getStartTime() == STARTING_DATE_1).isTrue();
        Truth.assertThat(capturedPathList.get(1).getEndTime() == END_DATE_1).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(0).latitude == LATITUDE_1_1).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(0).longitude == LONGITUDE_1_1).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(1).latitude == LATITUDE_1_2).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(1).longitude == LONGITUDE_1_2).isTrue();

        final List<Path> capturedSavedPathList = savedPathListToCapture.getValue();

        Truth.assertThat(capturedSavedPathList.size() == 2).isTrue();
        Truth.assertThat(capturedSavedPathList.get(1).getLatLngList().size() == 2).isTrue();
        Truth.assertThat(capturedSavedPathList.get(1).getName()).isEqualTo(" 2");
        Truth.assertThat(capturedSavedPathList.get(1).getStartTime() == STARTING_DATE_1).isTrue();
        Truth.assertThat(capturedSavedPathList.get(1).getEndTime() == END_DATE_1).isTrue();
        Truth.assertThat(capturedSavedPathList.get(1).getLatLngList().get(0).latitude == LATITUDE_1_1).isTrue();
        Truth.assertThat(capturedSavedPathList.get(1).getLatLngList().get(0).longitude == LONGITUDE_1_1).isTrue();
        Truth.assertThat(capturedSavedPathList.get(1).getLatLngList().get(1).latitude == LATITUDE_1_2).isTrue();
        Truth.assertThat(capturedSavedPathList.get(1).getLatLngList().get(1).longitude == LONGITUDE_1_2).isTrue();
    }

    @Test
    public void onListLoaded_default() {
        final List<LatLng> latLngList1 = new ArrayList<>();
        latLngList1.add(new LatLng(LATITUDE_1_1, LONGITUDE_1_1));
        latLngList1.add(new LatLng(LATITUDE_1_2, LONGITUDE_1_2));

        final List<LatLng> latLngList2 = new ArrayList<>();
        latLngList2.add(new LatLng(LATITUDE_2_1, LONGITUDE_2_1));
        latLngList2.add(new LatLng(LATITUDE_2_2, LONGITUDE_2_2));

        final List<Path> pathList = new ArrayList<>();
        pathList.add(new Path(PATH_NAME_1, latLngList1, STARTING_DATE_1, END_DATE_1));
        pathList.add(new Path(PATH_NAME_2, latLngList2, STARTING_DATE_2, END_DATE_2));

        state.pathsList = pathList;

        final Capture<List<Path>> pathListToCapture = Capture.newInstance();
        view.showPathList(capture(pathListToCapture));

        PowerMock.replayAll();

        presenter.onListLoaded();

        final List<Path> capturedPathList = pathListToCapture.getValue();

        Truth.assertThat(capturedPathList.size() == 2).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().size() == 2).isTrue();
        Truth.assertThat(capturedPathList.get(0).getName()).isEqualTo(PATH_NAME_1);
        Truth.assertThat(capturedPathList.get(0).getStartTime() == STARTING_DATE_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getEndTime() == END_DATE_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(0).latitude == LATITUDE_1_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(0).longitude == LONGITUDE_1_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(1).latitude == LATITUDE_1_2).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(1).longitude == LONGITUDE_1_2).isTrue();

        Truth.assertThat(capturedPathList.get(1).getName()).isEqualTo(PATH_NAME_2);
        Truth.assertThat(capturedPathList.get(1).getStartTime() == STARTING_DATE_2).isTrue();
        Truth.assertThat(capturedPathList.get(1).getEndTime() == END_DATE_2).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(0).latitude == LATITUDE_2_1).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(0).longitude == LONGITUDE_2_1).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(1).latitude == LATITUDE_2_2).isTrue();
        Truth.assertThat(capturedPathList.get(1).getLatLngList().get(1).longitude == LONGITUDE_2_2).isTrue();
    }

    @Test
    public void startTracking_printedMode() {
        state.recording = false;
        state.printedPath = true;

        view.fetchLocation();
        view.showStopTrackingMenu();
        PowerMock.replayAll();

        presenter.startTracking(0);
        Truth.assertThat(state.printedPath).isFalse();
        Truth.assertThat(state.trackingPath.getLatLngList().size()==0).isTrue();
        Truth.assertThat(state.tracking).isTrue();
    }

    @Test
    public void stopTracking_default() {
        state.tracking = true;

        view.showTrackingMenu();
        view.stopLocationFetch();

        PowerMock.replayAll();

        presenter.stopTracking();

    }

    @Test
    public void startRecording_printedMapMode() {
        state.printedPath = true;

        final List<LatLng> latLngList1 = new ArrayList<>();
        latLngList1.add(new LatLng(LATITUDE_1_1, LONGITUDE_1_1));
        latLngList1.add(new LatLng(LATITUDE_1_2, LONGITUDE_1_2));
        state.recordingPath = new Path(PATH_NAME_1, latLngList1, STARTING_DATE_1, END_DATE_1);

        final List<LatLng> latLngList2 = new ArrayList<>();
        latLngList2.add(new LatLng(LATITUDE_2_1, LONGITUDE_2_1));
        latLngList2.add(new LatLng(LATITUDE_2_2, LONGITUDE_2_2));
        state.trackingPath = new Path(PATH_NAME_2, latLngList2, STARTING_DATE_2, END_DATE_2);

        final Capture<List<LatLng>> trackedPathLocationsToCapture = Capture.newInstance();
        final Capture<List<LatLng>> recordedPathLocationsToCapture = Capture.newInstance();
        view.showMapPaths(capture(trackedPathLocationsToCapture), capture(recordedPathLocationsToCapture));

        view.showStopRecordingMenu();
        view.showTrackingMenu();
        view.fetchLocation();

        PowerMock.replayAll();

        presenter.startRecording(STARTING_DATE_3);

        Truth.assertThat(state.printedPath).isFalse();
        Truth.assertThat(state.trackingPath.getLatLngList().size()==0).isTrue();
        Truth.assertThat(state.tracking).isFalse();

        Truth.assertThat(state.recording).isTrue();
        Truth.assertThat(state.recordingPath.getLatLngList().size()==0).isTrue();
        Truth.assertThat(state.recordingPath.getStartTime()==STARTING_DATE_3).isTrue();

        final List<LatLng> capturedTrackedPathLocationsList = trackedPathLocationsToCapture.getValue();
        Truth.assertThat(capturedTrackedPathLocationsList.size() == 0).isTrue();

        final List<LatLng> capturedRecordedPathList = recordedPathLocationsToCapture.getValue();
        Truth.assertThat(capturedRecordedPathList.size() == 0).isTrue();
    }

    @Test
    public void stopRecording_default() {
        final List<LatLng> latLngList1 = new ArrayList<>();
        latLngList1.add(new LatLng(LATITUDE_1_1, LONGITUDE_1_1));
        latLngList1.add(new LatLng(LATITUDE_1_2, LONGITUDE_1_2));
        state.recordingPath = new Path(PATH_NAME_1, latLngList1, STARTING_DATE_1, END_DATE_1);

        final List<LatLng> latLngList2 = new ArrayList<>();
        latLngList2.add(new LatLng(LATITUDE_2_1, LONGITUDE_2_1));
        latLngList2.add(new LatLng(LATITUDE_2_2, LONGITUDE_2_2));
        state.trackingPath = new Path(PATH_NAME_2, latLngList2, STARTING_DATE_2, END_DATE_2);

        EasyMock.expect(stringProvider.getString(EasyMock.anyInt())).andReturn("").once();
        final Capture<List<Path>> savedPathListToCapture = Capture.newInstance();
        pathsRepository.savePaths(capture(savedPathListToCapture));

        final Capture<List<Path>> pathListToCapture = Capture.newInstance();
        view.showPathList(capture(pathListToCapture));
        view.showRecordMenu();
        view.stopLocationFetch();

        PowerMock.replayAll();

        presenter.stopRecording(END_DATE_3);

        final List<Path> capturedPathList = pathListToCapture.getValue();

        Truth.assertThat(capturedPathList.size() == 1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().size() == 2).isTrue();
        Truth.assertThat(capturedPathList.get(0).getName()).isEqualTo(" 1");
        Truth.assertThat(capturedPathList.get(0).getStartTime() == STARTING_DATE_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getEndTime() == END_DATE_3).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(0).latitude == LATITUDE_1_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(0).longitude == LONGITUDE_1_1).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(1).latitude == LATITUDE_1_2).isTrue();
        Truth.assertThat(capturedPathList.get(0).getLatLngList().get(1).longitude == LONGITUDE_1_2).isTrue();
    }

    @Test
    public void onMenuLoaded_recording() {
        state.recording = true;
        state.tracking = false;

        view.showStopRecordingMenu();
        view.showTrackingMenu();

        PowerMock.replayAll();

        presenter.initMenu();
    }

    @Test
    public void onMenuLoaded_tracking() {
        state.recording = false;
        state.tracking = true;

        view.showRecordMenu();
        view.showStopTrackingMenu();

        PowerMock.replayAll();

        presenter.initMenu();
    }

    @Test
    public void onResetLocationManagement_default() {
        state.trackingPath = new Path("x", new ArrayList<LatLng>(), 1, 1);
        state.recordingPath = new Path("x", new ArrayList<LatLng>(), 1, 1);
        state.tracking = true;
        state.recording = true;
        state.printedPath = true;

        view.showRecordMenu();
        view.showTrackingMenu();

        PowerMock.replayAll();

        presenter.onResetLocationManagement();

        Truth.assertThat(state.trackingPath.getName()).isEqualTo("");
        Truth.assertThat(state.trackingPath.getStartTime()).isEqualTo(0);
        Truth.assertThat(state.trackingPath.getEndTime()).isEqualTo(0);

        Truth.assertThat(state.recordingPath.getName()).isEqualTo("");
        Truth.assertThat(state.recordingPath.getStartTime()).isEqualTo(0);
        Truth.assertThat(state.recordingPath.getEndTime()).isEqualTo(0);

        Truth.assertThat(state.tracking).isFalse();
        Truth.assertThat(state.recording).isFalse();
        Truth.assertThat(state.printedPath).isFalse();
    }

}