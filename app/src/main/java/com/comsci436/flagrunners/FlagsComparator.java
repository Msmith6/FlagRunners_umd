package com.comsci436.flagrunners;

import java.util.Comparator;

/**
 * Created by Taemin on 2016-05-14.
 */
public class FlagsComparator implements Comparator<User> {
    @Override
    public int compare(User lhs, User rhs) {
        return rhs.getFlags().compareTo(lhs.getFlags());
    }
}
