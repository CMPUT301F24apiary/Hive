package com.example.hive;

/*

EVENT CLASS FOR TESTING PURPOSES ONLY

 */

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * @author Zach
 */
public class TestEvent implements Parcelable {

    private String title;
    private String date;
    private String time;
    private String cost;
    private long dateInMS;
    private String firebaseID;

    public TestEvent(String title, String date, String time, String cost, long longDate, String firebaseID) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.cost = cost;
        this.dateInMS = longDate;
        this.firebaseID = firebaseID;
    }

    protected TestEvent(@NonNull Parcel in) {
        title = in.readString();
        date = in.readString();
        time = in.readString();
        cost = in.readString();
        dateInMS = in.readLong();
        firebaseID = in.readString();
    }

    public static final Creator<TestEvent> CREATOR = new Creator<TestEvent>() {
        @Override
        public TestEvent createFromParcel(Parcel in) {
            return new TestEvent(in);
        }

        @Override
        public TestEvent[] newArray(int size) {
            return new TestEvent[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCost() {
        return cost;
    }

    public long getDateInMS() { return dateInMS; }

    public String getFirebaseID() { return firebaseID; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(cost);
        dest.writeLong(dateInMS);
        dest.writeString(firebaseID);
    }
}
