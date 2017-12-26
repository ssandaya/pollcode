/**
 * Created by sandaya on 6/30/2017.
 */

import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.UllagePressure
import groovy.io.FileType

import java.text.DateFormat
import java.text.SimpleDateFormat

class DbViewrGmtFilesULDaily {
    static String toYyyy_Mm_DdDate() {
        DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd")
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return gmtFormat.format(new Date());
    }

    static String toDdate(long timestamp) {
        DateFormat gmtFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//        String result = gmtFormat.format(new Date((long) timestamp * 1000))
        return gmtFormat.format(new Date((long) timestamp * 1000));
    }
    private static String toGmtDate(String unixTimeStamp)
    {
        //unix timestamps have GMT time zone.
//        DateFormat gmtFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        DateFormat gmtFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss");
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        //date obtained here is in IST on my system which needs to be converted into GMT.
        Date time = new Date(Long.valueOf(unixTimeStamp) * 1000);

        String result = gmtFormat.format(time);
        return result;
    }

    static void mainDateTest(args) {
        println toDdate(1496980793L)
        println toDdate(1497153598L)
        println toDdate(1497067209L)
    }

    static String toTsDate(String sdate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss")
        Date d = simpleDateFormat.parse(sdate);
//        Timestamp ts = Timestamp.valueOf( sdate )
        return d.getTime()/1000

    }
    static void main10(args) {
        println toTsDate("2017-06-08 23:59:53")  //1496980793L)
        println toTsDate("2017-06-10 23:59:58.0000")  //1497153598L)
        println toTsDate("2017-06-10 00:00:09.0000")  //1497067209L)
    }
    static void main(String[] args) {
        String headerLine = ""
        String dataLine = ""
        int tankNo

        String basepath = "C:/cygwin64/home/simulate/proc/polldata/dbviewer_20171209/"
        String destpath = "C:/cygwin64/home/simulate/proc/polldata/AVA-SiteData/"

        def count = 0
        def tempcount = 0
        def pressurecount = 0
        def list = []
        int lineno = 1

        def dailyfile0 = new File("", "")
        def dailyfile = new File("", "")
        def dir = new File(basepath)

        ArrayList<UllagePressure> ullagePressures = new ArrayList<>()
        ArrayList<MeterTemperature> meterTemperatures = new ArrayList<>()
        ArrayList headerarray = new ArrayList()
        boolean headerSet = false

        dir.eachFileRecurse (FileType.FILES) { file ->

            String filename  = file.getName()
            String filepath  = file.getPath()
            String fileparent = file.getParent()

            String dir_ip = fileparent.substring(fileparent.lastIndexOf("/") +1)
            String siteId = ""
            println "${fileparent}   ${filename}"
            if (fileparent.contains("252721")) {
                siteId = "838035"
            } else if (fileparent.contains("287078")) {
                siteId = "820810"
            } else if (fileparent.contains("298036")) {
                siteId = "820811"
            } else {
                println "Error: invalid siteId"
                return

            }


            String dirname   = filename.substring(0, filename.indexOf("_") + 1)
            String dtepart   = filename.substring(filename.lastIndexOf(" ")+1,filename.lastIndexOf("."))
            String dtepart2  = dtepart.substring(0,4) + "-" + dtepart.substring(4,6)  + "-" + dtepart.substring(6,8)
            String hdfilename2 = filename.substring(0, filename.lastIndexOf("_")+1)
            String filename2 = siteId + "_UllagePressure_" + dtepart +".csv"

            def folder = new File(destpath + siteId)

            if( !folder.exists() ) {
                folder.mkdirs()
            }

            dailyfile = new File(folder, filename2)

            lineno = 1
            pressurecount++
            ullagePressures = new ArrayList<>()

            file.text.eachLine {
                line -> println  "line : $lineno  $line";
                headerLine = "TIMESTAMP,DATE TIME,SENSORID,PRESSURE"
                headerarray = headerLine.split(",")
                headerSet = true

                ArrayList dataElement = line.split(",")
                int ts = Integer.parseInt( toTsDate(dataElement[1] + " " + dataElement[2]))

                UllagePressure ul = new UllagePressure()
                        .setTankId(Integer.valueOf("0")) // dummy value
                        .setTimeStamp(ts)
                        .setDdate(dataElement[1].toString().replaceAll("-","/").trim() +" "+dataElement[2].toString().trim())
                        .setPressure(Double.valueOf(dataElement[3]))

                ullagePressures.add(ul)
                lineno += 1
            }
            // Sort
            ullagePressures.sort{x,y->
                if(x.ddate == y.ddate){
                    x.tankId <=> y.tankId
                }else{
                    x.ddate <=> y.ddate
                }
            }

            for (UllagePressure ul in ullagePressures ) {
                String fn = siteId + "_UllagePressure_" + ul.getDdate().substring(0,10).replaceAll("/","-") +".csv"

                def dailyfile_new = new File(folder, fn)
                if (dailyfile_new.toString() != dailyfile.toString()) {
                    if (!dailyfile_new.exists()) {
                        dailyfile_new << headerarray.join(",") + "\n"
                        sleep(1000)
                    }
                    dailyfile_new << ul.toString()
                } else {
                    if (!dailyfile.exists()) {
                        dailyfile << headerarray.join(",") + "\n"
                        sleep(1000)
                    }
                    dailyfile << ul.toString()
                }

            }

            count++
        }

        println "Total Files: ${count; } Total Tempfiles: ${tempcount} Total Pressurefiles: ${pressurecount}"
    }
}
