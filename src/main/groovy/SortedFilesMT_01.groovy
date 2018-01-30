/**
 * Created by sandaya on 6/30/2017.
 */


import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.PollDataUtility
import com.veeder.ava.polldata.UllagePressure
import groovy.io.FileType

import java.text.SimpleDateFormat

class SortedFilesMT_01 {

    static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo
        String destpath = "C:/cygwin64/home/simulate/proc/polldata/SortedData_R20171225/" //+ PollDataUtility.toYyyyMmDdDate() + "/"
        String basepath = "C:/cygwin64/home/simulate/proc/polldata/UnsortedData_R20171225/" //+ PollDataUtility.toYyyyMmDdDate() + "/"

        def basefolder = new File(basepath)
        if( !basefolder.exists() ) {
            println "Processed gurps ${basepath} files not found"
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
        def dailyfile0 = new File("", "")
        def dailyfile = new File("", "")
        def dir = new File(basepath)

        ArrayList<UllagePressure> ullagePressures = new ArrayList<>()
        ArrayList<MeterTemperature> meterTemperatures = new ArrayList<>()
        ArrayList headerarray = new ArrayList()

        boolean headerSet = false
        int lineno = 1

        dir.eachFileRecurse(FileType.FILES) { file ->
            String filename = file.getName()
            String filename_date=filename.substring(filename.lastIndexOf("_")+1,filename.lastIndexOf("."))

            String filepath = file.getPath()

            String dirname = filename.substring(0, filename.indexOf("_"))
            def folder = new File(destpath + dirname)

            if (!folder.exists()) {
                folder.mkdirs()
            }
            dailyfile = new File(folder, filename)
            if (filename.contains("MeterTemperature")) {
                println "${filename}   ${dailyfile}"

                meterTemperatures = new ArrayList<>()

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
                                    .setDdate(dataElement[1])
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
                // check if merge is needed
                if (dailyfile.exists()) {
                    dailyfile << stringBuilder.toString()

                    def dailyLineNo = 1
                    def dailyHeader = ""
                    ArrayList<MeterTemperature> dailyMeterTemperatures = new ArrayList<>()
                    dailyfile.eachLine { dailyline ->
                        if (dailyLineNo == 1) {
                            dailyHeader = dailyline
                        } else {
                            ArrayList dataElement = dailyline.split(",")
                            MeterTemperature dailyMt = new MeterTemperature()
                                    .setTankId(Integer.valueOf(dataElement[2]))
                                    .setTimeStamp(Integer.valueOf(dataElement[0]))
                                    .setDdate(dataElement[1])
                                    .setTemperatures(new ArrayList<Double>([dataElement[3].toString().trim(), dataElement[4].toString().trim(), dataElement[5].toString().trim(), dataElement[6].toString().trim(), dataElement[7].toString().trim(), dataElement[8].toString().trim(), dataElement[9].toString().trim()]))


                            dailyMeterTemperatures.add(dailyMt)

                        }
                        dailyLineNo += 1
                    }
                    // Sort
                    dailyMeterTemperatures.sort{x,y->
                        if(x.ddate == y.ddate){
                            x.tankId <=> y.tankId
                        }else{
                            x.ddate <=> y.ddate
                        }
                    }

                    StringBuilder dailyStringBuilder = new StringBuilder()
                    MeterTemperature dailyMtbefore = new MeterTemperature()
                    for (MeterTemperature dailyMetertemp in dailyMeterTemperatures) {
                        if (dailyMtbefore.toString().equals(dailyMetertemp.toString())) {
                            dailyMtbefore = dailyMetertemp.clone()
                            continue
                        }
                        dailyStringBuilder.append(dailyMetertemp.toString())
                        dailyMtbefore = dailyMetertemp.clone()
                    }
                    dailyfile.delete()
                    dailyfile << dailyHeader
                    dailyfile << dailyStringBuilder.toString()
                } else {
                    if (!dailyfile.name.contains(PollDataUtility.toYyyyMmDdDate())) {
                        dailyfile << headerarray.join(",") + "\n"
                        dailyfile << stringBuilder.toString()
                    }
                }
            }
            count++
        }
        println "Total Files: ${count;} Total Tempfiles: ${tempcount} Total Pressurefiles: ${pressurecount}"
    }
}
