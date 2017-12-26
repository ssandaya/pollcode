/**
 * Created by sandaya on 6/30/2017.
 */

import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.UllagePressure
import groovy.io.FileType

import java.text.SimpleDateFormat

class DirFiles {
    static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
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
            String filename2 = filename.substring(0, filename.lastIndexOf("_")) + "_" + dtepart2 +".csv"

            def folder = new File(destpath + dirname)

            if( !folder.exists() ) {
                folder.mkdirs()
            }

            dailyfile = new File(folder, filename2)

            if (dailyfile.toString() !=  dailyfile0.toString() ) {
                if (dailyfile0.toString() != "\\") {
                    if (dailyfile0.toString().contains("Meter_Temperature")) {
                        if( !dailyfile0.exists() ) {
                            dailyfile0 << headerarray2.join(",") + "\n"
                        }

                        for (mt in meterTemperatures) {
                            dailyfile0 << mt.toString()
                        }


                    } else if (dailyfile0.toString().contains("Ullage_Pressure")) {
                        for (ul in ullagePressures) {
                            dailyfile0 << ul.toString()
                        }

                    }
                }

                ullagePressures = new ArrayList<>()
                meterTemperatures = new ArrayList<>()
                headerSet = false
                dailyfile0 = dailyfile
                lineno = 1

            }

            println "${folder}\\${filename2} ${dailyfile}"

            if (filename.contains("Meter_Temperature")) {
                lineno = 1
                tempcount++
                file.text.eachLine {
                    line -> println  "line : $lineno  $line";
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
                            }
                        }
                        else {
                            dataLine = line

                            ArrayList dataarray0 = dataLine.split(",")


                            int tankId = Integer.valueOf(dataarray0.head())
                            ArrayList dataarray = dataarray0.tail()
                            for (int i = 0; i < dataarray.size(); i += 8) {
                                ArrayList dataElement = dataarray.subList(i,i+8)
                                MeterTemperature mt = new MeterTemperature()
                                        .setTankId(tankId)
                                        .setTimeStamp(Integer.valueOf(dataElement[0]))
                                        .setDdate(MeterTemperature.calcTimeStamp(Integer.valueOf(dataElement[0])))
                                        .setTemperatures(new ArrayList<Double>([dataElement[1].toString().trim(), dataElement[2].toString().trim(), dataElement[3].toString().trim(), dataElement[4].toString().trim(), dataElement[5].toString().trim(), dataElement[6].toString().trim(), dataElement[7].toString().trim()]))

                                meterTemperatures.add(mt)



                            }

                        }
                        lineno += 1
                }
            } else if (filename.contains("Ullage_Pressure")) {
                pressurecount++

                file.text.eachLine {
                    line -> println  "line : $lineno  $line";
                        if ( lineno == 1 ) headerLine = line.replaceAll("\"","").toUpperCase() +"\n"
                        else dataLine = line
                        lineno += 1
                }
            }

            count++
        }

        if (dailyfile.toString().contains("Meter_Temperature")) {

//            meterTemperatures.sort{x,y->
//                if(x.timeStamp == y.timeStamp){
//                    x.tankId <=> y.tankId
//                }else{
//                    x.timeStamp <=> y.timeStamp
//                }
//            }

            if( !dailyfile0.exists() ) {
                dailyfile0 << headerarray2.join(",")
            }
            for (mt in meterTemperatures) {
                dailyfile0 << mt.toString()
            }


        } else if (dailyfile0.toString().contains("Ullage_Pressure")) {
            if( !dailyfile0.exists() ) {
                dailyfile0 << headerarray2.join(",")
            }
            for (ul in ullagePressures) {
                dailyfile0 << ul.toString()
            }

        }
        println "Total Files: ${count; } Total Tempfiles: ${tempcount} Total Pressurefiles: ${pressurecount}"
    }
}
