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

    public TestEvent(String title, String date, String time, String cost) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.cost = cost;
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
}
