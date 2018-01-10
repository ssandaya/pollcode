/**
 * Created by sandaya on 6/30/2017.
 */

import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.PollDataUtility
import com.veeder.ava.polldata.UllagePressure
import groovy.io.FileType

import java.text.DateFormat
import java.text.SimpleDateFormat

class DbViewrGmtFilesUL {


    static void main(String[] args) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo

        String basepath = "C:/cygwin64/home/simulate/proc/polldata/dbviewer_20180101/"  //+ PollDataUtility.toYyyyMmDdDate() + "/"
        String destpath = "C:/cygwin64/home/simulate/proc/polldata/ullage_20180101/"    //+ PollDataUtility.toYyyyMmDdDate() + "/"

        def count = 0
        def tempcount = 0
        def pressurecount = 0
        def list = []
        int lineno = 1

        def dailyfile0 = new File("", "")
        def dailyfile = new File("", "")
        def dir = new File(basepath)

        ArrayList<UllagePressure> ullagePressures = new ArrayList<>()
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

                UllagePressure ul = new UllagePressure()
                        .setTankId(Integer.valueOf("0")) // dummy value
                        .setTimeStamp(Integer.parseInt( PollDataUtility.toGmtTimestampDate(dataElement[1] + " " + dataElement[2])))
                        .setDdate(dataElement[1].toString().replaceAll("-","/").trim() +" " + dataElement[2].toString().trim())
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
