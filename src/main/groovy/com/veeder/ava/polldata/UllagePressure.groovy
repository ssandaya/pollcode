package com.veeder.ava.polldata

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import java.text.SimpleDateFormat
import groovy.transform.EqualsAndHashCode
import groovy.transform.AutoClone

/**
 * Created by sandaya on 7/2/2017.
 */

@AutoClone
@EqualsAndHashCode
@Builder(builderStrategy=SimpleStrategy)
class UllagePressure {
    int tankId
    int timeStamp
    String ddate
    double pressure

    @Override
    int hashCode() {
        return super.hashCode()
    }

    @Override
    boolean equals(Object obj) {

        return this.getTimeStamp()
    }

    @Override
    public String toString() {
        String str = ""  + timeStamp + "," + ddate + "," + tankId + "," +pressure + "\n"
        return  str

    }

    static void main(String[] args) {
        def ulpressure = new UllagePressure()
                .setTankId(1)
                .setTimeStamp(123456)
                .setDdate(PollDataUtility.toDdate(123456L))
                .setPressure(-6.23122d)


        print ulpressure.toString()


    }

}
