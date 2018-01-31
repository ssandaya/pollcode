/**
 * Created by simulate on 8/28/2017.
 */


import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.PollDataUtility
import com.veeder.ava.polldata.ProbeSamples
import groovy.io.FileType

import java.text.DateFormat
import java.text.SimpleDateFormat

class VpsUnsortedGmtFilesLtdPS {

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

    static void main(String[] args) {
        String Site_locale="Site" + args[0]

        String headerLine = ""
        String dataLine = ""
        int tankNo

        String destpath = "Q:/VpsUnsortedData_" + PollDataUtility.toYyyyMmDdDate() + "/"
        String basepath = "Q:/vps_" + PollDataUtility.toYyyyMmDdDate() + "/"

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

            if (filename.contains("ProbeSamples")) {
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
                                //"Tank","ProductCode","ProbeType","StatusFlags","SampleCount","FuelHeight","WaterHeight","Temperature","Timestamp"
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
                            /*

"Tank","ProductCode","ProbeType","StatusFlags","SampleCount","FuelHeight","WaterHeight","Temperature","Timestamp"
1,"1",3,0,17,       68.2013,        0.0000,       57.3560,       56.9737,       56.5074,       57.2024,       58.2447,       58.2016,1514219623
1,"1",3,0,17,       68.2014,        0.0000,       57.3563,       56.9738,       56.5072,       57.2024,       58.2444,       58.2019,1514219653
1,"1",3,0,17,       68.2013,        0.0000,       57.3563,       56.9737,       56.5073,       57.2023,       58.2439,       58.2023,1514219683
1,"1",3,0,14,       68.2013,        0.0000,       57.3563,       56.9738,       56.5076,       57.2024,       58.2428,       58.2024,1514219713
1,"1",3,0,19,       68.2015,        0.0000,       57.3559,       56.9738,       56.5077,       57.2028,       58.2424,       58.2028,1514219743
1,"1",3,0,16,       68.2013,        0.0000,       57.3560,       56.9740,       56.5081,       57.2028,       58.2426,       58.2027,1514219773
1,"1",3,0,18,       68.2015,        0.0000,       57.3564,       56.9742,       56.5085,       57.2023,       58.2427,       58.2023,1514219803
1,"1",3,0,16,       68.2015,        0.0000,       57.3568,       56.9743,       56.5088,       57.2025,       58.2428,       58.2021,1514219833
1,"1",3,0,18,       68.2018,        0.0000,       57.3571,       56.9743,       56.5088,       57.2027,       58.2433,       58.2021,1514219863
1,"1",3,0,16,       68.2012,        0.0000,       57.3575,       56.9745,       56.5088,       57.2029,       58.2435,       58.2021,1514219893


                             */
                            boolean bNull = false
                            for (int i=0; i<=13; i++) {
                                def el = dataElement[i]
                                if (dataElement[i] == null || dataElement[i] == "null") {
                                    bNull = true
                                }
                            }
                            if (!bNull) {
                                ProbeSamples ps = new ProbeSamples()
                                        .setTimeStamp(Integer.valueOf(dataElement[13]))
                                        .setDdate(PollDataUtility.toDdate(Integer.valueOf(dataElement[13])))
                                        .setTankId(Integer.valueOf(dataElement[0]))
                                        .setProductCode(Integer.valueOf(dataElement[1]))
                                        .setProbeType(Integer.valueOf(dataElement[2]))
                                        .setStatusFlags(Integer.valueOf(dataElement[3]))
                                        .setSampleCount(Integer.valueOf(dataElement[4]))
                                        .setFuelHeight(Double.valueOf(dataElement[5]))
                                        .setWaterHeight(Double.valueOf(dataElement[6]))

                                        .setTemperatures(new ArrayList<Double>([dataElement[7], dataElement[8], dataElement[9], dataElement[10], dataElement[11], dataElement[12]]))

                                String fn = hdfilename2 + ps.getDdate().substring(0, 10).replaceAll("/", "-") + ".csv"
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


                        }
                        lineno += 1
                }
            }
            count++
        }

        println "Total Files: ${count; } Total Tempfiles: ${tempcount} Total Pressurefiles: ${pressurecount}"
    }
}
