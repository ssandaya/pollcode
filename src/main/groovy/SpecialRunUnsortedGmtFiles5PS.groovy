/**
 * Created by simulate on 8/28/2017.
 */


import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.PollDataUtility
import com.veeder.ava.polldata.ProbeSample5sec
import groovy.io.FileType

import java.text.DateFormat
import java.text.SimpleDateFormat

class SpecialRunUnsortedGmtFiles5PS {

    private static String toGmtDate(String unixTimeStamp)
    {

        DateFormat gmtFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss");
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        //date obtained here is in IST on my system which needs to be converted into GMT.
        Date time = new Date(Long.valueOf(unixTimeStamp) * 1000);

        String result = gmtFormat.format(time);
        return result;
    }

    static void main(String[] args) {
        String headerLine = ""
        String dataLine = ""
        int tankNo

        String destpath = "C:/cygwin64/home/simulate/proc/polldata/UnsortedData_rerun_" + PollDataUtility.toYyyyMmDdDate() + "/"
        String basepath = "C:/cygwin64/home/simulate/proc/polldata/gurps_rerun_" + PollDataUtility.toYyyyMmDdDate() + "/"

        def count = 0
        def tempcount = 0
        def pressurecount = 0
        def list = []
        int lineno = 1

        def dailyfile0 = new File("", "")
        def dailyfile = new File("", "")
        def dir = new File(basepath)

        ArrayList<MeterTemperature> meterTemperatures = new ArrayList<>()
        ArrayList headerarray2 = new ArrayList()
        String hdr = ""
        boolean headerSet = false

        dir.eachFileRecurse (FileType.FILES) { file ->

            String filename  = file.getName()
            String filepath  = file.getPath()

            String dirname   = filename.substring(0, filename.indexOf("_"))
            String dtepart   = filename.substring(filename.lastIndexOf("_")+1,filename.lastIndexOf("-"))
            String dtepart2  = dtepart.substring(0,4) + "-" + dtepart.substring(4,6)  + "-" + dtepart.substring(6,8)
            String hdfilename2 = filename.substring(0, filename.lastIndexOf("_")+1)
            String filename2 = hdfilename2 + dtepart2 +".csv"

            def folder = new File(destpath + dirname)

            if( !folder.exists() ) {
                folder.mkdirs()
            }

            dailyfile = new File(folder, filename2)

            if (filename.contains("5secProbeSamples")) {
                println "${filename}   ${dailyfile}"

                lineno = 1
                tempcount++
                file.text.eachLine {
                    line -> /*println  "line : $lineno  $line";*/
                        if ( lineno == 1) {
                            if (!headerSet) {
                                // "Tank","ProductCode","ProbeType","StatusFlags","SampleCount","FuelHeight","WaterHeight","Density","Temperature","Timestamp"
                                //  0      1             2           3             4             5            6             7          8,9,10,11,12,13   14
//                                hdr = "TIMESTAMP,DATE,TANK NUM,PRODUCT CODE,PROBE TYPE,STATUS FLAGS,SAMPLE COUNT,FUEL HEIGHT,WATER HEIGHT,DENSITY,TEMP 1,TEMP 2,TEMP 3,TEMP 4,TEMP 5,TEMP 6"
                                hdr = "TIMESTAMP,DATE,TANK NUM,FUEL HEIGHT,WATER HEIGHT,TEMP 1,TEMP 2,TEMP 3,TEMP 4,TEMP 5,TEMP 6"
                                //            0         1                2            3          4            5            6           7            8       9      10     11     12     13     14
                                headerSet = true

                            }
                            if (!dailyfile.exists()) {
                                dailyfile << hdr + "\n"
                            }
                        }
                        else if (!line.empty) {
                            dataLine = line.replaceAll("\"","")
                            ArrayList<String> dataElement = dataLine.split(",").toList()
                            ProbeSample5sec ps = new ProbeSample5sec()
                                    .setTimeStamp(Integer.valueOf(dataElement[14]))
                                    .setDdate(PollDataUtility.toDdate(Integer.valueOf(dataElement[14])))
                                    .setTankId(Integer.valueOf(dataElement[0]))
                                    .setProductCode(Integer.valueOf(dataElement[1]))
                                    .setProbeType(Integer.valueOf(dataElement[2]))
                                    .setStatusFlags(Integer.valueOf(dataElement[3]))
                                    .setSampleCount(Integer.valueOf(dataElement[4]))
                                    .setFuelHeight(Double.valueOf(dataElement[5]))
                                    .setWaterHeight(Double.valueOf(dataElement[6]))
                                    .setDensity(Double.valueOf(dataElement[7]))

                                    .setTemperatures(new ArrayList<Double>([dataElement[8], dataElement[9], dataElement[10], dataElement[11], dataElement[12], dataElement[13]]))

                            String fn = hdfilename2 + ps.getDdate().substring(0,10).replaceAll("/","-") + ".csv"
//                                println fn

                            def dailyfile_new = new File(folder, fn)
                            if (dailyfile_new.toString() != dailyfile.toString()) {
                                if (!dailyfile_new.exists()) {
                                    dailyfile_new << hdr + "\n"
//                                    sleep(1000)
                                }
                                dailyfile_new << ps.toString()
                            } else {
                                if (!dailyfile.exists()) {
                                    dailyfile << headerarray2.join(",") + "\n"
//                                    sleep(1000)
                                }
                                dailyfile << ps.toString()

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
