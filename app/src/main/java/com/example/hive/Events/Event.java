package com.example.hive.Events;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import com.google.firebase.firestore.PropertyName;

/**
 * Class to represent an event. Implements <code>Parcelable</code> in order to be passed via <code>
 * Intent</code>.
 *
 * @author Zach
 *
 * @see Parcelable
 */
public class Event implements Parcelable {

    private String title;
    private String cost;
    private long startDateInMS;
    private long endDateInMS;
    private String firebaseID;
    private String description;
    private String location;
    private int numParticipants;
    private String poster;
    private long selectionDate;
    private Integer entrantLimit;
    private String duration;
    private boolean geolocation;
    private boolean replacementDrawAllowed;
    private HashMap<String, String> waitingList;
    private String waitingListId;
    private String qrCode;
    private boolean isLotteryDrawn;
    @PropertyName("cancelledlistID")
    private String cancelledListID;
    @PropertyName("finallistID")
    private String finalListID;

    /**
     * No-argument constructor for Firestore.
     */
    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    /**
     * Event constructor. Creates a new event object with provided parameters.
     *
     * @param title           String: The event title.
     * @param cost            String: The cost of the event. Should be in format "00.00"
     * @param startDateInMS   long: Start date in the format of milliseconds since epoch.
     * @param endDateInMS     long: End date in the format of milliseconds since epoch.
     * @param firebaseID      String: Nullable: The ID of the related event document in Firestore.
     * @param description     String: The event description.
     * @param numParticipants int: The number of participants to be selected for this event.
     * @param location        String: The location of the event, i.e. facility. Note that this will be converted to be an
     *                        instance of a Facility object in the future.
     * @param poster          String: Nullable: The download URL of the event poster related to this event that is stored
     *                        in Firebase cloud storage.
     * @param selectionDate   long: The date when participant selection occurs.
     * @param entrantLimit    Integer: Nullable: The maximum number of entrants allowed.
     * @param duration        String: The duration of the event.
     * @param geolocation     boolean: Whether geolocation is enabled for this event.
     * @param replacementDrawAllowed boolean: Whether replacement draws are allowed.
     * @param isLotteryDrawn  boolean: Whether the lottery has been drawn.
     */
    public Event(String title, String cost, long startDateInMS, long endDateInMS,
                 @Nullable String firebaseID, String description, int numParticipants,
                 String location, @Nullable String poster, long selectionDate,
                 @Nullable Integer entrantLimit, String duration, boolean geolocation,
                 boolean replacementDrawAllowed, boolean isLotteryDrawn) {
        this.title = title;
        this.cost = cost;
        this.startDateInMS = startDateInMS;
        this.endDateInMS = endDateInMS;
        this.firebaseID = firebaseID;
        this.description = description;
        this.location = location;
        this.numParticipants = numParticipants;
        this.poster = poster;
        this.selectionDate = selectionDate;
        this.entrantLimit = entrantLimit;
        this.duration = duration;
        this.geolocation = geolocation;
        this.replacementDrawAllowed = replacementDrawAllowed;
        this.waitingList = new HashMap<>();
        this.waitingListId = "";
        this.isLotteryDrawn = isLotteryDrawn;
    }

    /**
     * Required function to create an event object from a <code>Parcel</code>
     *
     * @param in Parcel: The Parcel object to unpack
     * @see Parcel
     */
    protected Event(@NonNull Parcel in) {
        this.title = in.readString();
        this.cost = in.readString();
        this.startDateInMS = in.readLong();
        this.endDateInMS = in.readLong();
        this.firebaseID = in.readString();
        this.description = in.readString();
        this.location = in.readString();
        this.numParticipants = in.readInt();
        this.selectionDate = in.readLong();
        this.poster = in.readString();
        this.entrantLimit = (Integer) in.readSerializable();
        this.duration = in.readString();
        this.geolocation = in.readInt() == 1;
        this.replacementDrawAllowed = in.readInt() == 1;
        this.waitingList = in.readHashMap(String.class.getClassLoader());
        this.waitingListId = in.readString();
        this.isLotteryDrawn = in.readInt() == 1;
        this.qrCode = in.readString();
        this.cancelledListID = in.readString();
        this.finalListID = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(cost);
        dest.writeLong(startDateInMS);
        dest.writeLong(endDateInMS);
        dest.writeString(firebaseID);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeInt(numParticipants);
        dest.writeLong(selectionDate);
        dest.writeString(poster);
        dest.writeSerializable(entrantLimit);
        dest.writeString(duration);
        dest.writeInt(geolocation ? 1 : 0);
        dest.writeInt(replacementDrawAllowed ? 1 : 0);
        dest.writeMap(waitingList);
        dest.writeString(waitingListId);
        dest.writeInt(isLotteryDrawn ? 1 : 0);
        dest.writeString(qrCode);
        dest.writeString(cancelledListID);
        dest.writeString(finalListID);
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

    /**
     * Getter for event title.
     *
     * @return The title of the event.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for event title.
     *
     * @param title String: The title to set for this event.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter to get start date in human readable format. Returns date as "mmm dd" i.e. "Jan 01"
     *
     * @return The date in human readable format
     */
    public String getStartDate() {
        return getDateAndTimeFromMS(this.startDateInMS)[0];
    }

    /**
     * Getter to get selection date in human readable format. Returns date as "mmm dd" i.e. "Jan 01"
     *
     * @return The date in human readable format
     */
    public String getSelectionDate() {
        return getDateAndTimeFromMS(this.selectionDate)[0];
    }

    /**
     * Setter for start date.
     *
     * @param startDate long: The start date in milliseconds since epoch.
     */
    public void setStartDate(long startDate) {
        this.startDateInMS = startDate;
    }

    /**
     * Getter to get end date in human readable format. Returns date as "mmm dd" i.e. "Jan 01"
     *
     * @return The date in human readable format
     */
    public String getEndDate() {
        return getDateAndTimeFromMS(this.endDateInMS)[0];
    }

    /**
     * Setter for end date.
     *
     * @param endDate long: The end date in milliseconds since epoch.
     */
    public void setEndDate(long endDate) {
        this.endDateInMS = endDate;
    }

    /**
     * Getter to get start time in human readable format. Returns time as "hh:mm" i.e. "09:00". Note
     * that this uses the 24Hr clock, i.e. 9:00 p.m. is "21:00"
     *
     * @return The time in human readable format
     */
    public String getStartTime() {
        return getDateAndTimeFromMS(this.startDateInMS)[1];
    }

    /**
     * Getter to get end time in human readable format. Returns time as "hh:mm" i.e. "09:00". Note
     * that this uses the 24Hr clock, i.e. 9:00 p.m. is "21:00"
     *
     * @return The time in human readable format
     */
    public String getEndTime() {
        return getDateAndTimeFromMS(this.endDateInMS)[1];
    }

    /**
     * Getter for event cost.
     *
     * @return The cost of the event.
     */
    public String getCost() {
        return cost;
    }

    /**
     * Setter for event cost.
     *
     * @param cost String: The cost to set for this event.
     */
    public void setCost(String cost) {
        this.cost = cost;
    }

    /**
     * Getter for entrant limit.
     *
     * @return The maximum number of entrants allowed for this event, or null if unlimited.
     */
    public Integer getEntrantLimit() {
        return entrantLimit;
    }

    /**
     * Getter for start time, in MS since epoch.
     *
     * @return The time in MS since epoch.
     */
    public long getStartDateInMS() {
        return startDateInMS;
    }

    /**
     * Getter for end time, in MS since epoch.
     *
     * @return The time in MS since epoch.
     */
    public long getEndDateInMS() {
        return endDateInMS;
    }

    /**
     * Getter for selection date in milliseconds.
     *
     * @return The selection date in milliseconds since epoch.
     */
    public long getSelectionDateInMS() {
        return selectionDate;
    }

    /**
     * Getter for firebase ID.
     *
     * @return This event's firebase ID - the ID of related event document in Firestore.
     */
    public String getFirebaseID() {
        return firebaseID;
    }

    /**
     * Getter for event description.
     *
     * @return This event's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for event duration.
     *
     * @return The duration of the event.
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Getter for lottery drawn status.
     *
     * @return boolean: Whether the lottery has been drawn for this event.
     */
    @PropertyName("isLotteryDrawn")
    public boolean isLotteryDrawn() {
        return isLotteryDrawn;
    }

    /**
     * Setter for lottery drawn status.
     *
     * @param lotteryDrawn boolean: The lottery drawn status to set.
     */
    @PropertyName("isLotteryDrawn")
    public void setLotteryDrawn(boolean lotteryDrawn) {
        this.isLotteryDrawn = lotteryDrawn;
    }

    /**
     * Getter for event location.
     *
     * @return This event's location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Getter for waiting list.
     *
     * @return HashMap containing user IDs and names of users on the waiting list.
     */
    public HashMap<String, String> getWaitingList() {
        return waitingList;
    }

    /**
     * Setter for waiting list.
     *
     * @param waitingList HashMap: The waiting list to set for this event.
     */
    public void setWaitingList(HashMap<String, String> waitingList) {
        this.waitingList = waitingList;
    }

    /**
     * Adds a user to the waiting list.
     *
     * @param userId String: The ID of the user to add.
     * @param userName String: The name of the user to add.
     */
    public void addToWaitingList(String userId, String userName) {
        if (waitingList != null) {
            waitingList.put(userId, userName);
        }
    }

    /**
     * Removes a user from the waiting list.
     *
     * @param userId String: The ID of the user to remove.
     */
    public void removeFromWaitingList(String userId) {
        if (waitingList != null) {
            waitingList.remove(userId);
        }
    }

    /**
     * Setter for waiting list ID.
     *
     * @param waitingListId String: The ID to set for the waiting list document.
     */
    @PropertyName("waiting-list-id")
    public void setWaitingListId(String waitingListId) {
        this.waitingListId = waitingListId;
    }

    /**
     * Getter for waiting list ID.
     *
     * @return String: The ID of the waiting list document in Firestore.
     */
    @PropertyName("waiting-list-id")
    public String getWaitingListId() {
        return waitingListId;
    }

    /**
     * Getter for this events poster download URL.
     *
     * @return The download URL for the poster related to this event, stored in Firebase Cloud Storage.
     */
    @PropertyName("poster")
    public String getPosterURL() {
        return poster;
    }

    /**
     * Getter for number of participants.
     *
     * @return The number of participants for this event.
     */
    public int getNumParticipants() {
        return numParticipants;
    }

    /**
     * Setter for number of participants.
     *
     * @param numParticipants int: The number of participants to set for this event.
     */
    public void setNumParticipants(int numParticipants) {
        this.numParticipants = numParticipants;
    }

    /**
     * Setter for firebase ID.
     *
     * @param firebaseID The Firebase ID to set this event's <code>firebaseID</code> to.
     */
    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    /**
     * Setter for poster URL.
     *
     * @param poster The URL to set this event's <code>posterURL</code> to.
     */
    @PropertyName("poster")
    public void setPoster(String poster) {
        this.poster = poster;
    }

    /**
     * Getter for geolocation setting.
     *
     * @param geolocation Whether geolocation is enabled for this event.
     */
    public void SetGeolocation(boolean geolocation) {
        this.geolocation = geolocation;
    }

    /**
     * Getter for geolocation setting.
     *
     * @return boolean: Whether geolocation is enabled for this event.
     */
    public boolean getGeolocation() {
        return geolocation;
    }

    /**
     * Getter for replacement draw setting.
     *
     * @return boolean: Whether replacement draw is allowed for this event.
     */
    public boolean isReplacementDrawAllowed() {
        return replacementDrawAllowed;
    }

    /**
     * Setter for replacement draw setting.
     *
     * @param replacementDrawAllowed boolean: Whether to allow replacement draws.
     */
    public void setReplacementDrawAllowed(boolean replacementDrawAllowed) {
        this.replacementDrawAllowed = replacementDrawAllowed;
    }

    /**
     * Getter for cancelled list ID.
     *
     * @return String: The ID of the cancelled list document in Firestore.
     */
    @PropertyName("cancelledlistID")
    public String getCancelledListID() {
        return cancelledListID;
    }

    /**
     * Setter for cancelled list ID.
     *
     * @param cancelledListID String: The ID to set for the cancelled list document.
     */
    @PropertyName("cancelledlistID")
    public void setCancelledListID(String cancelledListID) {
        this.cancelledListID = cancelledListID;
    }

    /**
     * Getter for final list ID.
     *
     * @return String: The ID of the final list document in Firestore.
     */
    @PropertyName("finallistID")
    public String getFinalListID() {
        return finalListID;
    }

    /**
     * Setter for final list ID.
     *
     * @param finalListID String: The ID to set for the final list document.
     */
    @PropertyName("finallistID")
    public void setFinalListID(String finalListID) {
        this.finalListID = finalListID;
    }

    /**
     * Getter for QR code.
     *
     * @return String: The QR code associated with this event.
     */
    public String getQrCode() {
        return qrCode;
    }

    /**
     * Setter for QR code.
     *
     * @param qrCode String: The QR code to set for this event.
     */
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    /**
     * Getter to return all members of this event.
     *
     * @return All of this event's data in a HashMap.
     */
    public HashMap<String, Object> getAll() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("title", this.title);
        data.put("cost", this.cost);
        data.put("description", this.description);
        data.put("location", this.location);
        data.put("endDateInMS", this.endDateInMS);
        data.put("startDateInMS", this.startDateInMS);
        data.put("numParticipants", this.numParticipants);
        data.put("duration",this.duration);
        data.put("selectionDate",this.selectionDate);
        data.put("geolocation", this.geolocation);
        data.put("replacementDrawAllowed", this.replacementDrawAllowed);
        data.put("entrantLimit", this.entrantLimit);
        data.put("poster", this.poster != null ? this.poster : "");
        return data;
    }

    /**
     * Gets date in dash-separated format (dd-MM-yyyy).
     *
     * @param whichDate String: Which date to format ("start", "end", or "selection").
     * @return The formatted date string, or empty string if invalid date type provided.
     */
    public String getDateInDashFormat(String whichDate) {
        switch (whichDate) {
            case "start": {
                Date date = new Date(startDateInMS);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                return sdf.format(date);
            }
            case "end": {
                Date date = new Date(endDateInMS);
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
     * @param dateInMS The date in MS since epoch to convert.
     * @return The String array containing the date and time.
     */
    public String[] getDateAndTimeFromMS(long dateInMS) {
        Date dateAsDate = new Date(dateInMS);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd-HH:mm", Locale.ENGLISH);
        String formatted = sdf.format(dateAsDate);
        return formatted.split("-");
    }
}