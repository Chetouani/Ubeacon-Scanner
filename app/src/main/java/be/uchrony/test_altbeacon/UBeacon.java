package be.uchrony.test_altbeacon;

import org.altbeacon.beacon.Beacon;

import java.util.Comparator;

/**
 * Created by Abdelhalim on 24/02/2015.
 */
public class UBeacon extends Beacon implements Comparable {

    public UBeacon(Beacon otherBeacon) {
        super(otherBeacon);
    }

    @Override
    public int compareTo(Object o) {

        UBeacon b1 = (UBeacon) o;
        if (b1.getDistance() == this.getDistance())
            return 0;
        else if (b1.getDistance() > this.getDistance())
            return -1;
        else
            return 1;
    }
}
