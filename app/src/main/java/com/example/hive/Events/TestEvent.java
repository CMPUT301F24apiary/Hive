package com.example.hive.Events;

/*

EVENT CLASS FOR TESTING PURPOSES ONLY

 */

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Zach
 */
public class TestEvent implements Parcelable {

    private final String title;
    private final String cost;
    private final long startDate;
    private final long endDate;
    private final String firebaseID;
    private final String description;
    private final String location;
    private String posterURL;

    public TestEvent(String title, String cost, long startDate, long endDate, String firebaseID,
                     String description, String location, @Nullable String posterURL) {
        this.title = title;
        this.cost = cost;
        this.startDate = startDate;
        this.endDate = endDate;
        this.firebaseID = firebaseID;
        this.description = description;
        this.location = location;
        this.posterURL = posterURL;
    }

    protected TestEvent(@NonNull Parcel in) {
        this.title = in.readString();
        this.cost = in.readString();
        this.startDate = in.readLong();
        this.endDate = in.readLong();
        this.firebaseID = in.readString();
        this.description = in.readString();
        this.location = in.readString();
        this.posterURL = in.readString();
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

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getPosterURL() {
        return posterURL;
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
        dest.writeString(posterURL);
    }

    private String[] getDateAndTimeFromMS(long dateInMS) {
        Date dateAsDate = new Date(dateInMS);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd-HH:mm", Locale.ENGLISH);
        String formatted = sdf.format(dateAsDate);
        return formatted.split("-");
    }
}
