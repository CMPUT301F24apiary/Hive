package com.example.hive;

/*

EVENT CLASS FOR TESTING PURPOSES ONLY

 */

/**
 * @author Zach
 */
public class TestEvent {

    private String title;
    private String date;
    private String time;
    private String cost;
    private long dateInMS;

    public TestEvent(String title, String date, String time, String cost, long longDate) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.cost = cost;
        this.dateInMS = longDate;
    }

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

}
