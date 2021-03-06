/**
 * Created by sandaya on 6/30/2017.
 */

import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.ProbeSample5sec
import com.veeder.ava.polldata.UllagePressure
import groovy.io.FileType

import java.text.DateFormat
import java.text.SimpleDateFormat

class UnsortedGmtFiles {
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
    static void main(String[] args) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo
//        String basepath = "C:/Users/sandaya/dev/tasks/polldata/"
//        String basepath = "X:\\AVA-TestData"
        String basepath = "C:\\Users\\sandaya\\dev\\tasks\\polldata\\"
        String destpath = "C:\\Users\\sandaya\\temp\\"

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
            String dtepart   = filename.substring(filename.lastIndexOf("_")+1,filename.lastIndexOf("-"))
            String dtepart2  = dtepart.substring(0,4) + "-" + dtepart.substring(4,6)  + "-" + dtepart.substring(6,8)
            String hdfilename2 = filename.substring(0, filename.lastIndexOf("_")+1)
            String filename2 = hdfilename2 + dtepart2 +".csv"

            def folder = new File(destpath + dirname)

            if( !folder.exists() ) {
                folder.mkdirs()
            }

            dailyfile = new File(folder, filename2)


            println "${filename}   ${dailyfile}"

            if (filename.contains("5secProbeSamples")) {
                lineno = 1
                tempcount++
                file.text.eachLine {
                    line -> /*println  "line : $lineno  $line";*/
                        if ( lineno == 1) {
                            if (!headerSet) {
                                headerLine = line.replaceAll("\"", "").toUpperCase()
                                ArrayList headerarray = headerLine.split(",")
                                headerarray2 = new ArrayList()
                                headerarray2.add(headerarray[1])
                                headerarray2.add("DATE TIME")
                                headerarray2.add(headerarray[0])
                                headerarray2.add(headerarray[2])
                                headerarray2.add(headerarray[3])
                                headerarray2.add(headerarray[4])
                                headerarray2.add(headerarray[5])
                                headerarray2.add(headerarray[6])
                                headerarray2.add(headerarray[7])
                                headerarray2.add(headerarray[8])

                                headerSet = true

                                if (!dailyfile.exists()) {
                                    dailyfile << headerarray2.join(",") + "\n"
                                }

                            }
                        }
                        else {
                            dataLine = line

                            ArrayList dataarray0 = dataLine.split(",")


                            int tankId = Integer.valueOf(dataarray0.head())
                            ArrayList dataarray = dataarray0.tail()
                            for (int i = 0; i < dataarray.size(); i += 8) {
                                ArrayList dataElement = dataarray.subList(i,i+8)
                                ProbeSample5sec ps = new ProbeSample5sec()
                                        .setTankId(tankId)
                                        .setTimeStamp(Integer.valueOf(dataElement[0]))
                                        .setDdate(MeterTemperature.calcTimeStamp(Integer.valueOf(dataElement[0])))
                                        .setFuelHeight(Double.valueOf(dataElement[4]))
                                        .setWaterHeight(Double.valueOf(dataElement[4]))
                                        .setTemperatures(new ArrayList<Double>([dataElement[1].toString().trim(), dataElement[2].toString().trim(), dataElement[3].toString().trim(), dataElement[4].toString().trim(), dataElement[5].toString().trim(), dataElement[6].toString().trim(), dataElement[7].toString().trim()]))

                                String fn = hdfilename2 + mt.getDdate().substring(0,10).replaceAll("/","-") + ".csv"
//                                println fn

                                def dailyfile_new = new File(folder, fn)
                                if (dailyfile_new.toString() != dailyfile.toString()) {
                                    if (!dailyfile_new.exists()) {
                                        dailyfile_new << headerarray2.join(",") + "\n"
                                        sleep(1000)
                                    }
                                    dailyfile_new << mt.toString()
                                } else {
                                    if (!dailyfile.exists()) {
                                        dailyfile << headerarray2.join(",") + "\n"
                                        sleep(1000)
                                    }
                                    dailyfile << mt.toString()

                                }

                            }

                        }
                        lineno += 1
                }
            } else if (filename.contains("Ullage_Pressure")) {
                lineno = 1
                pressurecount++
                dataLine = ""
                file.text.eachLine {
                    line -> /*println  "line : $lineno  $line";*/
                        if ( lineno == 1 ) headerLine = line.replaceAll("\"","").toUpperCase()
                        else dataLine += (!line.empty?line + "\n":"")
                        lineno += 1
                }

                ArrayList headerarray = headerLine.split(",")
                headerarray2 = new ArrayList()
                headerarray2.add(headerarray[2])
                headerarray2.add("DATE TIME")
                headerarray2.add(headerarray[0])
                headerarray2.add(headerarray[3])

                ArrayList dataarray = new ArrayList()

                for (line in dataLine.readLines()) {
                    ArrayList consarray = new ArrayList()
                    ArrayList linearray = line.split(",")
                    if (linearray[1]) {
                        consarray.add(linearray[1].toString().trim())
                        consarray.add(toDdate(new Long(linearray[1])))
                        consarray.add(linearray[0].toString().trim())
                        consarray.add(linearray[2].toString().trim())
                        dataarray.add(consarray)
//                        println "${linearray} :  ${linearray[1]}"
                    } else {
//                        println "Line Not OK"
                    }

                }



                for (ArrayList dat in dataarray ) {
                    String fn = hdfilename2 + dat[1].toString().substring(0,10).replaceAll("/","-") + ".csv"

                    def dailyfile_new = new File(folder, fn)
                    if (dailyfile_new.toString() != dailyfile.toString()) {
                        if (!dailyfile_new.exists()) {
                            dailyfile_new << headerarray2.join(",") + "\n"
                            sleep(1000)
                        }
                        dailyfile_new << dat.join(",") + "\n"
                    } else {
                        if (!dailyfile.exists()) {
                            dailyfile << headerarray2.join(",") + "\n"
                            sleep(1000)
                        }
                        dailyfile << dat.join(",") + "\n"
                    }

                }

            }

            count++
        }

        println "Total Files: ${count; } Total Tempfiles: ${tempcount} Total Pressurefiles: ${pressurecount}"
    }
}
