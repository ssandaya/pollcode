/**
 * Created by sandaya on 6/30/2017.
 */

import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.UllagePressure
import groovy.io.FileType

import java.text.SimpleDateFormat

class SortedFiles {
    static String toDdate(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        return simpleDateFormat.format(new Date((long) timestamp * 1000))
    }

    static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo
//        String basepath = "C:/Users/sandaya/dev/tasks/polldata/"
//        String basepath = "X:\\AVA-TestData"
        String destpath = "C:\\Users\\sandaya\\temp_sorted\\"
        String basepath = "C:\\Users\\sandaya\\temp\\"

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



        dir.eachFileRecurse(FileType.FILES) { file ->

            String filename = file.getName()
            String filepath = file.getPath()

            String dirname = filename.substring(0, filename.indexOf("_"))
//            String dtepart   = filename.substring(filename.lastIndexOf("_")+1,filename.lastIndexOf("-"))
//            String dtepart2  = dtepart.substring(0,4) + "-" + dtepart.substring(4,6)  + "-" + dtepart.substring(6,8)
//            String hdfilename2 = filename.substring(0, filename.lastIndexOf("_")+1)
//            String filename2 = hdfilename2 + dtepart2 +".csv"

            def folder = new File(destpath + dirname)

            if (!folder.exists()) {
                folder.mkdirs()
            }

            dailyfile = new File(folder, filename)


            println "${filename}   ${dailyfile}"

            if (filename.contains("Meter_Temperature")) {


                lineno = 1
                tempcount++
                file.text.eachLine {
                    line -> /*println  "line : $lineno  $line";*/
                        if (lineno == 1) {
                            if (!headerSet) {
                                headerLine = line.replaceAll("\"", "").toUpperCase()
                                headerarray = headerLine.split(",")

                                headerSet = true

                            }
                        } else {
                            ArrayList dataElement = line.split(",")

                            MeterTemperature mt = new MeterTemperature()
                                    .setTankId(Integer.valueOf(dataElement[2]))
                                    .setTimeStamp(Integer.valueOf(dataElement[0]))
                                    .setDdate(MeterTemperature.calcTimeStamp(Integer.valueOf(dataElement[0])))
                                    .setTemperatures(new ArrayList<Double>([dataElement[3].toString().trim(), dataElement[4].toString().trim(), dataElement[5].toString().trim(), dataElement[6].toString().trim(), dataElement[7].toString().trim(), dataElement[8].toString().trim(), dataElement[9].toString().trim()]))

                            meterTemperatures.add(mt)
                        }
                        lineno += 1
                }
                // Sort
                meterTemperatures.sort{x,y->
                    if(x.ddate == y.ddate){
                        x.tankId <=> y.tankId
                    }else{
                        x.ddate <=> y.ddate
                    }
                }
                StringBuilder stringBuilder = new StringBuilder()
                MeterTemperature mtbefore = new MeterTemperature()
                for (MeterTemperature metertemp in meterTemperatures) {
                    if (mtbefore.toString().equals(metertemp.toString())) {
                        mtbefore = metertemp.clone()
                        continue
                    }

                    stringBuilder.append(metertemp.toString())
                    mtbefore = metertemp.clone()
                }
                dailyfile << headerarray.join(",") + "\n"
                sleep(1000)
                dailyfile << stringBuilder.toString()
                sleep(1000)


            } else if (filename.contains("Ullage_Pressure")) {
                lineno = 1
                pressurecount++

                file.text.eachLine {
                    line -> /*println  "line : $lineno  $line";*/
                        if (lineno == 1) {
                            headerLine = line.replaceAll("\"", "").toUpperCase()
                            headerarray = headerLine.split(",")

                            headerSet = true                        }
                        else {
                            ArrayList dataElement = line.split(",")
                            UllagePressure ul = new UllagePressure()
                                    .setTankId(Integer.valueOf(dataElement[2]))
                                    .setTimeStamp(Integer.valueOf(dataElement[0]))
                                    .setDdate(MeterTemperature.calcTimeStamp(Integer.valueOf(dataElement[0])))
                                    .setPressure(Double.valueOf(dataElement[3]))

                            ullagePressures.add(ul)
                        }
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
                StringBuilder stringBuilder = new StringBuilder()
                UllagePressure upbefore = new UllagePressure()
                for (UllagePressure ullagepressure in ullagePressures) {
                    if (upbefore.toString().equals(ullagepressure.toString())) {
                        upbefore = ullagepressure.clone()
                        continue
                    }

                    stringBuilder.append(ullagepressure.toString())
                    upbefore = ullagepressure.clone()
                }
                dailyfile << headerarray.join(",") + "\n"
                sleep(1000)
                dailyfile << stringBuilder.toString()
                sleep(1000)

            }
            count++
        }


        println "Total Files: ${count;} Total Tempfiles: ${tempcount} Total Pressurefiles: ${pressurecount}"
    }

}
