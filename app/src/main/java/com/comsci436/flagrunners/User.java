package com.comsci436.flagrunners;

public class User {
    private String name;
    private long flagsCaptured;
    private double distance;

    public User(String name, long flagsCaptured, double distance) {
        this.name = name;
        this.flagsCaptured = flagsCaptured;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }
    public void setName(String nameText) {
        name = nameText;
    }

    public String getFlags() {
        return String.valueOf(flagsCaptured);
    }
    public void setFlags(int flags) {
        this.flagsCaptured = flags;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
}