/**
 * Created by sandaya on 6/30/2017.
 */

import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.PollDataUtility
import com.veeder.ava.polldata.UllagePressure
import groovy.io.FileType

import java.text.DateFormat
import java.text.SimpleDateFormat

class CorrectGmtUnsortedGmtFilesMT {

    static String toYyyyMmDdDate() {
        DateFormat gmtFormat = new SimpleDateFormat("yyyyMMdd")
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//        String result = gmtFormat.format(new Date((long) timestamp * 1000))
        return gmtFormat.format(new Date());
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


    static void mainYyyyMmDd(args) {
        println toYyyyMmDdDate()
    }
    static void main(String[] args) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo
//        String basepath = "C:/Users/sandaya/dev/tasks/polldata/"
//        String basepath = "X:/AVA-TestData"
//        String basepath = "C:/Users/sandaya/dev/tasks/polldata/"
//        String destpath = "C:/Users/sandaya/temp/"
//        String destpath = "C:/cygwin64/home/simulate/proc/polldata/UnsortedMtData_" + toYyyyMmDdDate() + "/"
//        String basepath = "c:/cygwin64/home/simulate/proc/polldata/gurps_" + toYyyyMmDdDate() + "/"

//        String destpath = "C:/cygwin64/home/simulate/proc/polldata_gmtcorrected/"
//        String basepath = "C:/cygwin64/home/simulate/proc/polldata_gmterror/"
//        String destpath = "C:/cygwin64/home/simulate/proc/polldata/UnsortedMtData_20171004/" //+ PollDataUtility.toYyyyMmDdDate() + "/"
//        String basepath = "C:/cygwin64/home/simulate/proc/polldata/gurps_20171004/" //+ PollDataUtility.toYyyyMmDdDate() + "/"
        String destpath = "C:/cygwin64/home/simulate/proc/polldata/UnsortedMtData_828950_20171004B/"
        String basepath = "C:/cygwin64/home/simulate/proc/polldata/metertemps_828950_20171004/"


        def basefolder = new File(basepath)
        if( !basefolder.exists() ) {
            println basepath
            println "Processed gurps files not found"
            return
        }

        def destfolder = new File(destpath)
        if( !destfolder.exists() ) {
            destfolder.mkdirs()
        }

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
        ArrayList headerarray2 = new ArrayList()
        boolean headerSet = false



        dir.eachFileRecurse (FileType.FILES) { file ->

            String filename  = file.getName()
            String filepath  = file.getPath()

            String dirname   = filename.substring(0, filename.indexOf("_"))
            String dtepart   = filename.substring(filename.lastIndexOf("_")+1,filename.lastIndexOf("."))
            String dtepart2  = dtepart
            String hdfilename2 = filename.substring(0, filename.lastIndexOf("_")+1)
            String filename2 = hdfilename2 + dtepart2 +".csv"

            def folder = new File(destpath + dirname)

            if( !folder.exists() ) {
                folder.mkdirs()
            }

            dailyfile = new File(folder, filename2)




            if (filename.contains("MeterTemperature")) {
                println "${filename}   ${dailyfile}"

                lineno = 1
                tempcount++
                file.text.eachLine {
                    line -> /*println  "line : $lineno  $line";*/
                        if ( lineno == 1) {
                            if (!headerSet) {
                                headerLine = line + "\n"

                                headerSet = true

                                if (!dailyfile.exists()) {
                                    dailyfile << headerLine
                                }

                            }
                        }
                        else {
                            dataLine = line

                            ArrayList dataarray = dataLine.split(",")
                            ArrayList dataElement = dataarray
                            MeterTemperature mt = new MeterTemperature()
                                    .setTankId(Integer.valueOf(dataElement[2]))
                                    .setTimeStamp(Integer.valueOf(dataElement[0]))
                                    .setDdate(PollDataUtility.toDdate(new Long(dataElement[0])))
                                    .setTemperatures(new ArrayList<Double>([dataElement[3].toString().trim(), dataElement[4].toString().trim(), dataElement[5].toString().trim(), dataElement[6].toString().trim(), dataElement[7].toString().trim(), dataElement[8].toString().trim(), dataElement[9].toString().trim()]))

                            String fn = hdfilename2 + mt.getDdate().substring(0,10).replaceAll("/","-") + ".csv"
//                                println fn

                            def dailyfile_new = new File(folder, fn)
                            if (dailyfile_new.toString() != dailyfile.toString()) {
                                if (!dailyfile_new.exists()) {
                                    dailyfile_new << headerLine
                                    sleep(1000)
                                }
                                dailyfile_new << mt.toString()
                            } else {
                                if (!dailyfile.exists()) {
                                    dailyfile << headerLine
                                    sleep(1000)
                                }
                                dailyfile << mt.toString()

                            }


                        }
                        lineno += 1
                }
            }

            count++
        }

        println "Total Files: ${count; } Total Tempfiles: ${tempcount} Total Pressurefiles: ${pressurecount}"
    }
}
