package com.comsci436.flagrunners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thomasyang on 5/13/16.
 */
public class Player {
    private String username;
    private String email;
    private int flagsCaptured;
    private int flagsDeployed;

    private double distanceTraveled;

    private List<String> friendsList;

    private HashMap<String, Integer> capturedByMap;
    private HashMap<String, Integer> capturedFromMap;

    public Player() {

    }

    public Player(String name, String e, String UID) {
        // Initialize the data
        username = name;
        email = e;
        flagsCaptured = 0;
        flagsDeployed = 0;

        distanceTraveled = 0.0;

        friendsList = new ArrayList<String>();
        capturedByMap = new HashMap<String, Integer>();
        capturedFromMap = new HashMap<String, Integer>();

        // Set the first index as the user themselves.
        // Must need as Firebase does not allow empty storage.
        friendsList.add(UID);
        capturedByMap.put(UID, 0);
        capturedFromMap.put(UID, 0);
    }

    // Getter methods to allow Firebase to access items
    public String getUsername() { return username; }

    public String getEmail() { return email; }

    public long getFlagsCaptured() { return flagsCaptured; }

    public long getFlagsDeployed() { return flagsDeployed; }

    public double getDistanceTraveled() { return distanceTraveled; }

    public List<String> getFriendsList() { return friendsList; }

    public HashMap<String, Integer> getCapturedByMap() { return capturedByMap; }

    public HashMap<String, Integer> getCapturedFromMap() { return capturedFromMap; }
}
