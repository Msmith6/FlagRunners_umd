package com.comsci436.flagrunners;

import java.util.Comparator;

/**
 * Created by Taemin on 2016-05-14.
 */
public class DistanceComparator implements Comparator<User> {
    @Override
    public int compare(User lhs, User rhs) {
        if (rhs.getDistance() > lhs.getDistance()){ return 1; }
        else if (rhs.getDistance() == lhs.getDistance()){ return 0;}
        else{ return -1;}
    }
}