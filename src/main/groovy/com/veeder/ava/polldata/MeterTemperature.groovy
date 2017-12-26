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
class MeterTemperature {
    int tankId
    int timeStamp
    String ddate
    ArrayList<Double> temperatures = new ArrayList<>()

    @Override
    public String toString() {
        String temps = strTemperatures()
        String str = ""  + timeStamp + "," + ddate + "," + tankId + temps + "\n"
        return  str

    }

    private String strTemperatures() {
        String str = ""
        for (temp in temperatures) {
            str  +=  "," + temp
        }
        return str
    }


    static void main(String[] args) {
        def metertemps = new MeterTemperature()
                .setTankId(1)
                .setTimeStamp(123456)
                .setDdate(PollDataUtility.toDdate(123456L))
                .setTemperatures(new ArrayList<Double>([65.3433, 56.2345, 45.2344, 45.2333, 56.2343, 67.3222, 56.3421]))

        print metertemps.toString()


    }

}
