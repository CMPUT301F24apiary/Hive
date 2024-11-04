package com.example.hive;

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
    private String description;
    private String location;
    private int numParticipants;
    private long startDateTime;
    private long endDateTime;
    private String firebaseID;

    public TestEvent(String title, String date, String time, String cost, String description,
                     String location, int numParticipants, long startDateTime, long endDateTime, String firebaseID) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.cost = cost;
        this.description = description;
        this.location = location;
        this.numParticipants = numParticipants;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.firebaseID = firebaseID;
    }

    protected TestEvent(@NonNull Parcel in) {
        title = in.readString();
        date = in.readString();
        time = in.readString();
        cost = in.readString();
        description = in.readString();
        location = in.readString();
        numParticipants = in.readInt();
        startDateTime = in.readLong();
        endDateTime = in.readLong();
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

    // Getters for all fields
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getCost() { return cost; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public int getNumParticipants() { return numParticipants; }
    public long getStartDateTime() { return startDateTime; }
    public long getEndDateTime() { return endDateTime; }
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
        dest.writeString(description);
        dest.writeString(location);
        dest.writeInt(numParticipants);
        dest.writeLong(startDateTime);
        dest.writeLong(endDateTime);
        dest.writeString(firebaseID);
    }
}
