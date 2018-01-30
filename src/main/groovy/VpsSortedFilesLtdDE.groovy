/**
 * Created by sandaya on 6/30/2017.
 */


import com.veeder.ava.polldata.DimEvent
import com.veeder.ava.polldata.PollDataUtility
import groovy.io.FileType

import java.text.SimpleDateFormat

class VpsSortedFilesLtdDE {

    static void main(String[] args) {
        String Site_locale=args[0]

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo

        String destpath = "C:/cygwin64/home/simulate/proc/polldata/VpsSortedData_" + PollDataUtility.toYyyyMmDdDate() + "/"
        String basepath = "C:/cygwin64/home/simulate/proc/polldata/VpsUnsortedData_" + PollDataUtility.toYyyyMmDdDate() + "/${Site_locale}/"

        def basefolder = new File(basepath)
        if( !basefolder.exists() ) {
            println basepath
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

        ArrayList<DimEvent> dimEvents = new ArrayList<>()
        ArrayList headerarray = new ArrayList()

        boolean headerSet = false
        int lineno = 1

        dir.eachFileRecurse(FileType.FILES) { file ->

            String filename = file.getName()
            String filepath = file.getPath()

            String dirname = filename.substring(0, filename.indexOf("_"))
            def folder = new File(destpath + dirname)

            if (!folder.exists()) {
                folder.mkdirs()
            }

            dailyfile = new File(folder, filename)

            if (filename.contains("DimEvents")) {
                println "${filename}   ${dailyfile}"

                lineno = 1
                pressurecount++
                dimEvents = new ArrayList<>()

                file.text.eachLine {
                    line -> /*println  "line : $lineno  $line";*/
                        if (lineno == 1) {
                            headerLine = line.replaceAll("\"", "").toUpperCase()
                            headerarray = headerLine.split(",")

                            headerSet = true                        }
                        else {
                            ArrayList dataElement = line.split(",")
                            DimEvent de = new DimEvent()
                                    .setTimeStamp(Integer.valueOf(dataElement[0]))
                                    .setDdate(dataElement[1])
                                    .setFuelPos(Integer.valueOf(dataElement[2]))
                                    .setEventType(Boolean.valueOf(dataElement[3]))
                                    .setErrorFlag(Integer.valueOf(dataElement[4]))
                                    .setNumMeters(Integer.valueOf(dataElement[5]))
                                    .setMeter1(Integer.valueOf(dataElement[6]))
                                    .setTransVol1(Double.valueOf(dataElement[7]))
                                    .setTotalVol1(Double.valueOf(dataElement[8]))
                                    .setMeter2(Integer.valueOf(dataElement[9]))
                                    .setTransVol2(Double.valueOf(dataElement[10]))
                                    .setTotalVol2(Double.valueOf(dataElement[11]))

                            dimEvents.add(de)
                        }
                        lineno += 1
                }
                // Sort
                dimEvents.sort{x,y->
                    if(x.ddate == y.ddate){
                        x.fuelPos <=> y.fuelPos
                    }else{
                        x.ddate <=> y.ddate
                    }
                }

                StringBuilder stringBuilder = new StringBuilder()
                DimEvent debefore = new DimEvent()
                for (DimEvent dimEvent in dimEvents) {
                    if (debefore.toString().equals(dimEvent.toString())) {
                        debefore = dimEvent.clone()
                        continue
                    }

                    stringBuilder.append(dimEvent.toString())
                    debefore = dimEvent.clone()
                }

                // check if merge needed
                if (dailyfile.exists()) {
                    dailyfile << stringBuilder.toString()

                    def dailyLineNo = 1
                    def dailyHeader = ""
                    ArrayList<DimEvent> dailyDimEvents = new ArrayList<>()
                    dailyfile.eachLine { dailyline ->
                        if (dailyLineNo == 1) {
                            dailyHeader = dailyline
                        } else {
                            ArrayList dataElement = dailyline.split(",")
                            DimEvent dailyDE = new DimEvent()
                                    .setTimeStamp(Integer.valueOf(dataElement[0]))
                                    .setDdate(dataElement[1])
                                    .setFuelPos(Integer.valueOf(dataElement[2]))
                                    .setEventType(Boolean.valueOf(dataElement[3]))
                                    .setErrorFlag(Integer.valueOf(dataElement[4]))
                                    .setNumMeters(Integer.valueOf(dataElement[5]))
                                    .setMeter1(Integer.valueOf(dataElement[6]))
                                    .setTransVol1(Double.valueOf(dataElement[7]))
                                    .setTotalVol1(Double.valueOf(dataElement[8]))
                                    .setMeter2(Integer.valueOf(dataElement[9]))
                                    .setTransVol2(Double.valueOf(dataElement[10]))
                                    .setTotalVol2(Double.valueOf(dataElement[11]))

                            dailyDimEvents.add(dailyDE)
                        }
                        dailyLineNo += 1
                    }
                    // Sort
                    dimEvents.sort{x,y->
                        if(x.ddate == y.ddate){
                            x.fuelPos <=> y.fuelPos
                        }else{
                            x.ddate <=> y.ddate
                        }
                    }
                    StringBuilder dailyStringBuilder = new StringBuilder()
                    DimEvent dailyDebefore = new DimEvent()
                    for (DimEvent dailyDimEvent in dailyDimEvents) {
                        if (dailyDebefore.toString().equals(dailyDimEvent.toString())) {
                            dailyDebefore = dailyDimEvent.clone()
                            continue
                        }

                        dailyStringBuilder.append(dailyDimEvent.toString())
                        dailyDebefore = dailyDimEvent.clone()
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
