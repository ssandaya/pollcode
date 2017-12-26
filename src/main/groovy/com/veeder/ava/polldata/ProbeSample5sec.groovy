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
class ProbeSample5sec {
    //"Tank","ProductCode","ProbeType","StatusFlags","SampleCount","FuelHeight","WaterHeight","Density","Temperature","Timestamp"


    String ddate
    int timeStamp
    int tankId
    int productCode
    int probeType
    int statusFlags
    int sampleCount
    double fuelHeight
    double waterHeight
    double density

    ArrayList<Double> temperatures = new ArrayList<>()

    @Override
    public String toString() {
        String str = ""  + timeStamp + "," + ddate + "," + tankId +"," + fuelHeight + "," + waterHeight + strTemperatures() + "\n"
        return  str
    }

    private String strTemperatures() {
        String str = ""
        for (temp in temperatures) {
            str  +=  "," + temp.toString().trim()
        }
        return str
    }


    static void main(String[] args) {
        def ps5sec = new ProbeSample5sec()
                .setTankId(1)
                .setTimeStamp(123456)
                .setDdate(PollDataUtility.toDdate(new Long(123456)))
                .setTemperatures(new ArrayList<Double>([65.3433, 56.2345, 45.2344, 45.2333, 56.2343, 67.3222, 56.3421]))

        print ps5sec.toString()


    }

}
