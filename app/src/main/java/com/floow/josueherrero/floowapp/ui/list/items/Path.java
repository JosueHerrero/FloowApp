package com.floow.josueherrero.floowapp.ui.list.items;

import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.List;

/**
 * Created by JosuéManuel on 22/06/2017.
 *
 * This is our data model class.
 */

@Parcel(Parcel.Serialization.BEAN)
public final class Path {

    private String name;
    private long startTime;
    private long endTime;
    private List<LatLng> latLngList;
    private boolean expanded = false;

    public Path() {}

    @ParcelConstructor
    public Path(
            final String name,
            final List<LatLng> latLngList,
            final long startTime,
            final long endTime) {

        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.latLngList = latLngList;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<LatLng> getLatLngList() {
        return latLngList;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setLatLngList(final List<LatLng> latLngList) {
        this.latLngList = latLngList;
    }

    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(final long endTime) {
        this.endTime = endTime;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(final boolean expanded) {
        this.expanded = expanded;
    }

}