package com.theslof;

import java.util.Comparator;

public class BallComparator implements Comparator<Ball> {
    @Override
    public int compare(Ball o1, Ball o2) {
        if(o2.getCenterY() > o1.getCenterY())
            return 1;
        if(o2.getCenterY() < o1.getCenterY())
            return -1;
        return 0;
    }
}
