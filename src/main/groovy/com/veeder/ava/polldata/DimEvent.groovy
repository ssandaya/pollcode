package com.veeder.ava.polldata

import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import java.text.SimpleDateFormat

/**
 * Created by sandaya on 7/2/2017.
 */

@AutoClone
@EqualsAndHashCode
@Builder(builderStrategy=SimpleStrategy)
class DimEvent {
    int timeStamp
    String ddate
    int fuelPos
    boolean eventType
    int errorFlag
    int numMeters
    int meter1
    double transVol1
    double totalVol1
    int meter2
    double transVol2
    double totalVol2




    @Override
    public String toString() {
        String str = ""  + timeStamp + "," + ddate + "," + fuelPos + "," + eventType + "," + errorFlag + "," + numMeters + "," +
                meter1 + "," + transVol1 + "," + totalVol1 + ","+ meter2 + "," + transVol2 + "," + totalVol2 + "\n"
        return  str

    }


/*
    int timeStamp
    String ddate
    int fuelPos
    boolean eventType
    int errorFlag
    int numMeters
    int meter1
    double transVol1
    double totalVol1
    int meter2
    double transVol2
    double totalVol2

 */
    static void main(String[] args) {
        def dimEvent = new DimEvent()
            .setTimeStamp(Integer.valueOf("123456"))
            .setDdate(PollDataUtility.toDdate(123456L))
            .setFuelPos(1001)
            .setEventType(true)
            .setErrorFlag(0)
            .setNumMeters(1)
            .setMeter1(0)
            .setTransVol1(14.2440004348755)
            .setTotalVol1(449515.99)
            .setMeter2(2)
            .setTransVol2(28.2440004348755)
            .setTotalVol2(341234.76)

        print dimEvent.toString()
    }
}
