package com.example.hive.Events;

import android.app.WallpaperInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Class to represent an event. Implements <code>Parcelable</code> in order to be passed via <code>
 * Intent</code>.
 *
 * @author Zach
 *
 * @see Parcelable
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
    private final long selectionDate;
    private final Integer entrantLimit;
    private final String duration;
    private final boolean geolocationOn;
    private final boolean replacementDrawOn;

    /**
     * Event constructor. Creates a new event object with provided parameters.
     *
     *
     * @param title
     * String: The event title.
     * @param cost
     * String: The cost of the event. Should be in format "00.00"
     * @param startDate
     * long: Start date in the format of milliseconds since epoch.
     * @param endDate
     * long: End date in the format of milliseconds since epoch.
     * @param firebaseID
     * String: Nullable: The ID of the related event document in Firestore.
     * @param description
     * String: The event description.
     * @param numParticipants
     * int: The number of participants to be selected for this event.
     * @param location
     * String: The location of the event, i.e. facility. Note that this will be converted to be an
     * instance of a Facility object in the future.
     * @param posterURL
     * String: Nullable: The download URL of the event poster related to this event that is stored
     * in Firebase cloud storage.
     */
    public Event(String title, String cost, long startDate, long endDate,
                 @Nullable String firebaseID, String description, int numParticipants,
                 String location, @Nullable String posterURL, long selectionDate,
                 @Nullable Integer entrantLimit, String duration, boolean geolocationOn,
                 boolean replacementDrawOn) {
        this.title = title;
        this.cost = cost;
        this.startDate = startDate;
        this.endDate = endDate;
        this.firebaseID = firebaseID;
        this.description = description;
        this.location = location;
        this.numParticipants = numParticipants;
        this.posterURL = posterURL;
        this.selectionDate = selectionDate;
        this.entrantLimit = entrantLimit;
        this.duration = duration;
        this.geolocationOn = geolocationOn;
        this.replacementDrawOn = replacementDrawOn;
    }

    /**
     * Required function to create an event object from a <code>Parcel</code>
     *
     * @param in
     * Parcel: The Parcel object to unpack
     *
     * @see Parcel
     */
    protected Event(@NonNull Parcel in) {
        this.title = in.readString();
        this.cost = in.readString();
        this.startDate = in.readLong();
        this.endDate = in.readLong();
        this.firebaseID = in.readString();
        this.description = in.readString();
        this.location = in.readString();
        this.numParticipants = in.readInt();
        this.selectionDate = in.readLong();
        this.posterURL = in.readString();
        this.entrantLimit = (Integer) in.readSerializable();
        this.duration = in.readString();
        this.geolocationOn = in.readInt() == 1;
        this.replacementDrawOn = in.readInt() == 1;
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
        dest.writeLong(selectionDate);
        dest.writeString(posterURL);
        dest.writeSerializable(entrantLimit);
        dest.writeString(duration);
        dest.writeInt(geolocationOn ? 1 : 0);
        dest.writeInt(replacementDrawOn ? 1 : 0);
    }

    /**
     * Required function to create or unpack an event parcel.
     */
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

    /**
     * Getter to get start date in human readable format. Returns date as "mmm dd" i.e. "Jan 01"
     *
     * @return
     * The date in human readable format
     */
    public String getStartDate() {
        return getDateAndTimeFromMS(this.startDate)[0];
    }
    public String getSelectionDate() {
        return getDateAndTimeFromMS(this.selectionDate)[0];
    }

    


    /**
     * Getter to get end date in human readable format. Returns date as "mmm dd" i.e. "Jan 01"
     *
     * @return
     * The date in human readable format
     */
    public String getEndDate() {
        return getDateAndTimeFromMS(this.endDate)[0];
    }

    /**
     * Getter to get start time in human readable format. Returns time as "hh:mm" i.e. "09:00". Note
     * that this uses the 24Hr clock, i.e. 9:00 p.m. is "21:00"
     *
     * @return
     * The time in human readable format
     */
    public String getStartTime() {
        return getDateAndTimeFromMS(this.startDate)[1];
    }

    /**
     * Getter to get end time in human readable format. Returns time as "hh:mm" i.e. "09:00". Note
     * that this uses the 24Hr clock, i.e. 9:00 p.m. is "21:00"
     *
     * @return
     * The time in human readable format
     */
    public String getEndTime() {
        return getDateAndTimeFromMS(this.endDate)[1];
    }

    /**
     * Getter for event cost.
     *
     * @return
     * The cost of the event.
     */
    public String getCost() {
        return cost;
    }

    public Integer getEntrantLimit(){
        return entrantLimit; }

    /**
     * Getter for start time, in MS since epoch.
     *
     * @return
     * The time in MS since epoch.
     */
    public long getStartDateInMS() { return startDate; }

    /**
     * Getter for end time, in MS since epoch.
     *
     * @return
     * The time in MS since epoch.
     */
    public long getEndDateInMS() { return endDate; }

    /**
     * Getter for firebase ID.
     *
     * @return
     * This event's firebase ID - the ID of related event document in Firestore.
     */
    public String getFirebaseID() { return firebaseID; }

    /**
     * Getter for event description.
     *
     * @return
     * This event's description.
     */
    public String getDescription() {
        return description;
    }

    public String getDuration(){
        return duration;
    }

    /**
     * Getter for event location.
     *
     * @return
     * This event's location.
     */
    public String getLocation() {
        return location;
    }


    /**
     * Getter for this events poster download URL.
     *
     * @return
     * The download URL for the poster related to this event, stored in Firebase Cloud Storage.
     */
    public String getPosterURL() {
        return posterURL;
    }

    public int getNumParticipants() {
        return numParticipants;
    }

    /**
     * Setter for firebase ID.
     *
     * @param firebaseID
     * The Firebase ID to set this event's <code>firebaseID</code> to.
     */
    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    /**
     * Setter for poster URL.
     *
     * @param posterURL
     * The URL to set this event's <code>posterURL</code> to.
     */
    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    /**
     * Getter to return all members of this event.
     *
     * @return
     * All of this event's data in a HashMap.
     */
    public HashMap<String, Object> getAll() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("title", this.title);
        data.put("cost", this.cost);
        data.put("description", this.description);
        data.put("location", this.location);
        data.put("endDateInMS", this.endDate);
        data.put("startDateInMS", this.startDate);
        data.put("numParticipants", this.numParticipants);
        data.put("duration",this.duration);
        data.put("selectionDate",this.selectionDate);
        data.put("geolocation", this.geolocationOn);
        data.put("replacementDrawAllowed", this.replacementDrawOn);
        data.put("entrantLimit", this.entrantLimit);
        data.put("poster", this.posterURL != null ? this.posterURL : "");
        return data;
    }

    public String getDateInDashFormat(String whichDate) {
        switch (whichDate) {
            case "start": {
                Date date = new Date(startDate);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                return sdf.format(date);
            }
            case "end": {
                Date date = new Date(endDate);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                return sdf.format(date);
            }
            case "selection": {
                Date date = new Date(selectionDate);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                return sdf.format(date);
            }
            default:
                Log.e("Event Class - getDateInDashFormat", "Invalid date provided" + whichDate);
                return "";
        }
    }

    /**
     * Converts a date & time in MS since epoch to a human readable format. Constructed as
     * "mmm dd-hh:mm". Returned as an array whose first element is date, second is time.
     *
     * @param dateInMS
     * The date in MS since epoch to convert.
     * @return
     * The String array containing the date and time.
     */
    private String[] getDateAndTimeFromMS(long dateInMS) {
        Date dateAsDate = new Date(dateInMS);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd-HH:mm", Locale.ENGLISH);
        String formatted = sdf.format(dateAsDate);
        return formatted.split("-");
    }

    /**
     * Generates a QR code image from a string.
     *
     * Reference: https://github.com/afarber/android-questions/blob/master/QREncoder/app/src/main/java/de/afarber/qrencoder/MainActivity.java
     * @param width
     * @return
     * @throws WriterException These are exceptions that may occur when encoding a barcode using the Writer framework (from docs)
     */
    public Bitmap generateQRCode(int width, int height) throws WriterException {
        // error with QR generation may be because firebaseId is not set; check for that
        String data = firebaseID;

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);
        BarcodeEncoder encoder = new BarcodeEncoder();
        return encoder.createBitmap(matrix);
    }
}
