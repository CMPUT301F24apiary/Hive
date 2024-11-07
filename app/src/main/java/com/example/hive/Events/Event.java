package com.example.hive.Events;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author Zach
 */
public class Event implements Parcelable {

    private final String title;
    private final String cost;
    private final long startDate;
    private final long endDate;
    private String firebaseID;
    private final String description;
    private final String location;
    private final int numParticipants;
    private String posterURL;

    public Event(String title, String cost, long startDate, long endDate,
                 @Nullable String firebaseID, String description, int numParticipants,
                 String location, @Nullable String posterURL) {
        this.title = title;
        this.cost = cost;
        this.startDate = startDate;
        this.endDate = endDate;
        this.firebaseID = firebaseID;
        this.description = description;
        this.location = location;
        this.numParticipants = numParticipants;
        this.posterURL = posterURL;
    }

    protected Event(@NonNull Parcel in) {
        this.title = in.readString();
        this.cost = in.readString();
        this.startDate = in.readLong();
        this.endDate = in.readLong();
        this.firebaseID = in.readString();
        this.description = in.readString();
        this.location = in.readString();
        this.numParticipants = in.readInt();
        this.posterURL = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return getDateAndTimeFromMS(this.startDate)[0];
    }

    public String getEndDate() {
        return getDateAndTimeFromMS(this.endDate)[0];
    }

    public String getStartTime() {
        return getDateAndTimeFromMS(this.startDate)[1];
    }

    public String getEndTime() {
        return getDateAndTimeFromMS(this.endDate)[1];
    }

    public String getCost() {
        return cost;
    }

    public long getStartDateInMS() { return startDate; }

    public long getEndDateInMS() { return endDate; }

    public String getFirebaseID() { return firebaseID; }

    public int getNumParticipants() {
        return numParticipants;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public HashMap<String, Object> getAll() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("title", this.title);
        data.put("cost", this.cost);
        data.put("description", this.description);
        data.put("location", this.location);
        data.put("endDateInMS", this.endDate);
        data.put("startDateInMS", this.startDate);
        data.put("numParticipants", this.numParticipants);
        data.put("poster", this.posterURL != null ? this.posterURL : "");
        return data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(cost);
        dest.writeLong(startDate);
        dest.writeLong(endDate);
        dest.writeString(firebaseID);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeInt(numParticipants);
        dest.writeString(posterURL);
    }

    private String[] getDateAndTimeFromMS(long dateInMS) {
        Date dateAsDate = new Date(dateInMS);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd-HH:mm", Locale.ENGLISH);
        String formatted = sdf.format(dateAsDate);
        return formatted.split("-");
    }
}
