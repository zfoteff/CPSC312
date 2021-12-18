package com.cs312.jumpshot;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Event {

    private long id;
    String eventName;
    String startTime;
    String location;
    String photoFileName;
    ArrayList<Bitmap> bitmapArray;

    public Event() {
        id = -1;
        eventName = "EMPTY NAME";
        startTime = "EMPTY START";
        location = "EMPTY LOCATION";
        photoFileName = "placeholderimage";
        bitmapArray = new ArrayList<Bitmap>();

    }

    public Event(long id, String eventName, String startTime, String location, String photoFileName) {
        this.id = id;
        this.eventName = eventName;
        this.startTime = startTime;
        this.location = location;
        this.photoFileName = photoFileName;

    }

    public long getId() {return id; }
    public void setId(long newId) { id = newId; }
    public String getEventName() {
        return eventName;
    }
    public void setEventName(String newEventName) { eventName = newEventName; }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String newStartTime) { startTime = newStartTime; }
    public String getLocation() {
        return location;
    }
    public void setLocation(String newLocation) { location = newLocation; }
    public String getPhotoFileName() {
        return photoFileName;
    }
    public void setPhotoFileName(String newPhotoFileName) { photoFileName = newPhotoFileName; }
    public void addToBitmapArray(Bitmap bitmap) { bitmapArray.add(bitmap); }
    public ArrayList<Bitmap> getBitmapArray() { return bitmapArray; }

    @Override
    public String toString() { return "Event Name: " + eventName + ", Start Time: " + startTime +
            ", Location: " + location + ", Photo File Name: " + photoFileName; }
}
