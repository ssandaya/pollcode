/**
 * Created by sandaya on 6/30/2017.
 */

import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.PollDataUtility
import com.veeder.ava.polldata.UllagePressure
import groovy.io.FileType

import java.text.DateFormat
import java.text.SimpleDateFormat

class CorrectGmtUnsortedGmtFilesUP {

    private static String toGmtDate(String unixTimeStamp) {
        //unix timestamps have GMT time zone.
//        DateFormat gmtFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        DateFormat gmtFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss");
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        //date obtained here is in IST on my system which needs to be converted into GMT.
        Date time = new Date(Long.valueOf(unixTimeStamp) * 1000);

        String result = gmtFormat.format(time);
        return result;
    }

    static String toYyyyMmDdDate() {
        DateFormat gmtFormat = new SimpleDateFormat("yyyyMMdd")
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//        String result = gmtFormat.format(new Date((long) timestamp * 1000))
        return gmtFormat.format(new Date());
    }

    static void main(String[] args) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo
//        String basepath = "C:/Users/sandaya/dev/tasks/polldata/"
//        String basepath = "X:\\AVA-TestData"
//        String basepath = "C:\\Users\\sandaya\\dev\\tasks\\polldata\\"
//        String destpath = "C:\\Users\\sandaya\\temp\\"
//        String basepath = "X:\\AVA-TestData\\"
//        String destpath = "X:\\AVA-TestUnsortedGmtDataUP\\"
//        String destpath = "C:/cygwin64/home/simulate/proc/polldata/UnsortedUpData_" + toYyyyMmDdDate() + "/"
//        String basepath = "c:/cygwin64/home/simulate/proc/polldata/gurps_" + toYyyyMmDdDate() + "/"

        String destpath = "C:/cygwin64/home/simulate/proc/polldata_gmtcorrected/"
        String basepath = "C:/cygwin64/home/simulate/proc/polldata_gmterror/"

        def basefolder = new File(basepath)
        if (!basefolder.exists()) {
            println basepath
            println "Processed gurps ${basepath} files not found"
            return
        }

        def destfolder = new File(destpath)
        if (!destfolder.exists()) {
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



        dir.eachFileRecurse(FileType.FILES) { file ->

            String filename = file.getName()
            String filepath = file.getPath()

            String dirname = filename.substring(0, filename.indexOf("_"))
            String dtepart = filename.substring(filename.lastIndexOf("_") + 1, filename.lastIndexOf("."))
            String dtepart2 = dtepart
            String hdfilename2 = filename.substring(0, filename.lastIndexOf("_") + 1)
            String filename2 = hdfilename2 + dtepart2 + ".csv"

            def folder = new File(destpath + dirname)

            if (!folder.exists()) {
                folder.mkdirs()
            }

            dailyfile = new File(folder, filename2)

            if (filename.contains("UllagePressure")) {
                println "${filename}   ${dailyfile}"
                lineno = 1
                pressurecount++
                dataLine = ""
                file.text.eachLine {
                    line -> /*println  "line : $lineno  $line";*/
                        if (lineno == 1) {
                            if (!headerSet) {
                                headerLine = line + "\n"

                                headerSet = true

                                if (!dailyfile.exists()) {
                                    dailyfile << headerLine
                                }

                            }
                        } else {
                            dataLine = line

                            ArrayList dataarray = dataLine.split(",")
                            ArrayList dataElement = dataarray
                            UllagePressure up = new UllagePressure()
                                    .setTankId(Integer.valueOf(dataElement[2]))
                                    .setTimeStamp(Integer.valueOf(dataElement[0]))
                                    .setDdate(PollDataUtility.toDdate(new Long(dataElement[0])))
                                    .setPressure(Double.valueOf(dataElement[3]))

                            String fn = hdfilename2 + up.getDdate().substring(0, 10).replaceAll("/", "-") + ".csv"
//                                println fn

                            def dailyfile_new = new File(folder, fn)
                            if (dailyfile_new.toString() != dailyfile.toString()) {
                                if (!dailyfile_new.exists()) {
                                    dailyfile_new << headerLine
                                    sleep(1000)
                                }
                                dailyfile_new << up.toString()
                            } else {
                                if (!dailyfile.exists()) {
                                    dailyfile << headerLine
                                    sleep(1000)
                                }
                                dailyfile << up.toString()

                            }


                        }
                        lineno += 1

                }

                count++
            }


        }
        println "Total Files: ${count;} Total Tempfiles: ${tempcount} Total Pressurefiles: ${pressurecount}"
    }
}
