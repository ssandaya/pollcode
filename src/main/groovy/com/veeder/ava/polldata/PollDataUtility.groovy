package com.veeder.ava.polldata

import jdk.nashorn.internal.ir.LiteralNode

import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat

import groovy.time.TimeCategory


class PollDataUtility {
    static String toDdate(long timestamp) {
        DateFormat gmtFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
//        String result = gmtFormat.format(new Date((long) timestamp * 1000))
        return gmtFormat.format(new Date((long) timestamp * 1000))
    }

    static String toYyyyMmDdDate() {
        DateFormat gmtFormat = new SimpleDateFormat("yyyyMMdd")
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
//        String result = gmtFormat.format(new Date((long) timestamp * 1000))
        return gmtFormat.format(new Date())
    }
    static String toYyyyMmDdDateMinus(int d) {
        DateFormat gmtFormat = new SimpleDateFormat("yyyyMMdd")
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
//        String result = gmtFormat.format(new Date((long) timestamp * 1000))
        return gmtFormat.format(new Date() - d)
    }


    static String toGmtTimestampDate(String sdate) {
        SimpleDateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss")
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
        Date d = gmtFormat.parse(sdate)

//        Timestamp ts = Timestamp.valueOf( sdate )
        return d.getTime()/1000

    }

    static String toTimestampDate(String sdate) {
//        SimpleDateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss")
//        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//        Date d = gmtFormat.parse(sdate);
        Timestamp ts = Timestamp.valueOf( sdate )
        return ts

    }

    static Date toDate(String sdate) {
        SimpleDateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss")
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
        Date d = gmtFormat.parse(sdate)
        return d

    }
    static String toStringTimeStamp(Date ddate) {
        SimpleDateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss")
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
        String sd = gmtFormat.format(ddate)
        return sd
    }

    static String calculateBegOf30sec(String sdate) {
        String srdate
        if (get30secBraket(sdate).equals("gte0lt30")) {
            srdate = sdate.substring(0,17) + "00"
        } else {
            srdate = sdate.substring(0,17)+"30"
        }
        srdate
    }

    static String calculateEndOf30sec(String sdate) {
        Date ddate =  toDate(sdate)
        Date enddate = new Date()
        String srdate = ""
        String rtnDate = ""
        if (get30secBraket(toStringTimeStamp(ddate)) == "gte0lt30") {
            use( TimeCategory ) {
                srdate = sdate.substring(0,17) + "00"
                enddate = toDate(srdate) + 29.seconds
            }
        } else {
            use( TimeCategory ) {
                srdate = sdate.substring(0,17)+"30"
                enddate = toDate(srdate) + 29.seconds
            }
        }
        rtnDate = PollDataUtility.toStringTimeStamp(enddate)
        rtnDate
    }

    static String get30secBraket(String strDate) {
        def char17 = strDate.charAt(17).toString()
        if (Integer.valueOf(char17) < 3) {
            return "gte0lt30"
        } else if (Integer.valueOf(char17) < 6) {
            return "gte30lt60"
        }
        return "Not Valid"
    }

    static avgArrayOfDoubles(ArrayList<Double> args1, String timestamp) {
        if (args1.size() < 1)
        {
            println "You need to provide at least one numeric argument."
            System.exit(-1)
        }
        int argsize = args1.size()
        println "args1 size: ${argsize}"
        println "args1: ${timestamp} ${args1}"
        int numberItems = args1.size()
        double sum = 0.0
        for (item in args1)
        {

           sum = sum + Double.valueOf(item)
        }
        println "${sum} ${numberItems}"
        if (timestamp == "2017/08/01 00:00:00") {
            double rtnVal = sum/numberItems

        }
        sum/numberItems
    }

    static mean_mode_etc () {
        def args1 = [1.23,1.46,1.67,1.98, 1.98, 1.46]
        if (args1.size() < 1)
        {
            println "You need to provide at least one numeric argument."
            System.exit(-1)
        }
        println "args1: ${args1}"
        // Place passed-in number arguments in List
        def values = new ArrayList<Double>()
        for (i in args1)
        {
            try
            {
                values.add(new Double(i))
            }
            catch (NumberFormatException numberFormatEx)
            {
                println "Ignoring non-numeric parameter: ${i}"
            }
        }
        Collections.sort(values)

        // Begin collecting metadata about the data
        def numberItems = values.size()
        def sum = 0
        def modeMap = new HashMap<Double, Integer>()
        for (item in values)
        {
            sum += item
            if (modeMap.get(item) == null)
            {
                modeMap.put(item, 1)
            }
            else
            {
                def count = modeMap.get(item) + 1
                modeMap.put(item, count)
            }
        }
        println modeMap

        def mode = new ArrayList<Integer>()
        def modeCount = 0
        modeMap.each()
                { key, value ->
                    if (value > modeCount) { mode.clear(); mode.add(key); modeCount = value}
                    else if (value == modeCount) { mode.add(key) }
                };

        println mode

        def mean = sum / numberItems
        def midNumber = (int)(numberItems/2)
        def median = numberItems %2 != 0 ? values[midNumber] : (values[midNumber] + values[midNumber-1])/2
        def minimum = Collections.min(values)
        def maximum = Collections.max(values)

        println "You provided ${numberItems} numbers (${values}):"
        println "\tMean: ${mean}"
        println "\tMedian: ${median}"
        println "\tMode: ${mode}"
        println "\tMinimum: ${minimum}"
        println "\tMaximum: ${maximum}"
        println "\tRange: ${maximum - minimum}"

        def sumv = 1.23 + 1.46 + 1.46 + 1.67 + 1.98 + 1.98
        def avg = sumv/6
        println "avg: ${avg}"


    }

    static void main(args) {
        println toDdate(1496980793L)  //2017/06/09 03:59:53
        println toDdate(1497153598L)  //2017/06/11 03:59:58
        println toDdate(1497067209L)  //2017/06/10 04:00:09
        println()
        println toGmtTimestampDate("2017-09-04 00:00:00")       //1504483200
        println toGmtTimestampDate("2017-09-04 04:55:30")       //1504500930
        println toGmtTimestampDate("2017-06-10 00:00:09")       //1497052809
        println("should equal previous")
        println toDdate(1504483200L)  //2017/06/09 03:59:53
        println toDdate(1504500930L)  //2017/06/11 03:59:58
        println toDdate(1497052809L)  //2017/06/10 04:00:09
        println("yyyymmdd")
        println(toYyyyMmDdDate())
        println(toYyyyMmDdDateMinus(1))

        println "toTimestampDate"
        println toTimestampDate("2017-09-04 00:00:00")       //1504483200
        println toTimestampDate("2017-09-04 00:00:00")       //
        println toTimestampDate("2017-09-04 04:55:30")       //1504500930
        println toTimestampDate("2017-06-10 00:00:09")       //1497052809

        println get30secBraket("2017-09-04 00:00:00")       //1504483200
        println get30secBraket("2017-09-04 00:20:00")       //
        println get30secBraket("2017-09-04 04:45:30")       //1504500930
        println get30secBraket("2017-06-10 00:57:09")       //1497052809

        println toDate("2017-09-04 00:00:00")       //1504483200
        println toDate("2017-09-04 00:20:00")       //
        println toDate("2017-09-04 04:45:30")       //1504500930
        println toDate("2017-06-10 00:57:09")       //1497052809

        println toStringTimeStamp(toDate("2017-09-04 00:00:00"))       //1504483200
        println toStringTimeStamp(toDate("2017-09-04 00:20:00"))       //
        println toStringTimeStamp(toDate("2017-09-04 04:45:30"))       //1504500930
        println toStringTimeStamp(toDate("2017-06-10 00:57:09"))       //1497052809

        def d1 = toDate("2017-09-04 00:00:00")       //1504483200
        def d2 = toDate("2017-09-04 00:00:30")       //
        def d3 = toDate("2017-09-04 00:01:23")       //1504500930

        String record_timestamp = "2017-09-04 00:01:23"
        println "test"
        println record_timestamp.substring(0,17) + "00"
        println record_timestamp.substring(0,14) + record_timestamp.substring(14,16) + ":00"

        Date date30secBegin = toDate(record_timestamp.substring(0,17) + "00" )
        Date date30secEnd = toDate(record_timestamp.substring(0,17) + "30" )


        println "begin: " + toStringTimeStamp(date30secBegin)
        println "end  : " + toStringTimeStamp(date30secEnd)
        println "end2 : " + toStringTimeStamp(date30secEnd)

        if (d3 >= d1 && d3 < d2) {
            println "Looks good!"

        } else {
            println "Looks better!"
        }

        println calculateBegOf30sec("2017-09-04 00:00:00") // == "2017-09-04 00:00:00"
        println calculateEndOf30sec("2017-09-04 00:00:00") // == "2017-09-04 00:00:29"
        println calculateBegOf30sec("2017-09-04 00:01:40") // == "2017-09-04 00:01:30"
        println calculateEndOf30sec("2017-09-04 00:01:40") // == "2017-09-04 00:02:00"
        println calculateBegOf30sec("2017-09-04 00:59:40") // == "2017-09-04 00:59:30"
        println calculateEndOf30sec("2017-09-04 00:59:40") //.equals("2017-09-04 01:00:00")
        println calculateBegOf30sec("2017-09-04 23:59:59") //.equals("2017-09-04 00:59:30")
        println calculateEndOf30sec("2017-09-04 23:59:59") //.equals("2017-09-04 01:00:00")

        println toTimestampDate("2017-09-04 00:59:59")

        def fh = new ArrayList<Double>()
        fh << 1.23
        fh << 1.46
        fh << 1.67
        fh << 1.98
        fh << 1.98
        fh << 1.46
        println avgArrayOfDoubles(fh, "2017-09-04 00:59:59")
    }

}