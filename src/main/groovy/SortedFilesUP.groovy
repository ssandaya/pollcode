/**
 * Created by sandaya on 6/30/2017.
 */

import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.PollDataUtility
import com.veeder.ava.polldata.UllagePressure
import groovy.io.FileType

import java.text.DateFormat
import java.text.SimpleDateFormat

class SortedFilesUP {

    static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo

        String destpath = "C:/cygwin64/home/simulate/proc/polldata/SortedData_" + PollDataUtility.toYyyyMmDdDate() + "/"
        String basepath = "C:/cygwin64/home/simulate/proc/polldata/UnsortedData_" + PollDataUtility.toYyyyMmDdDate() + "/"

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

        ArrayList<UllagePressure> ullagePressures = new ArrayList<>()
        ArrayList<MeterTemperature> meterTemperatures = new ArrayList<>()
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

            if (filename.contains("UllagePressure")) {
                println "${filename}   ${dailyfile}"

                lineno = 1
                pressurecount++
                ullagePressures = new ArrayList<>()

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
                                    .setDdate(dataElement[1])
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

                // check if merge needed
                if (dailyfile.exists()) {
                    dailyfile << stringBuilder.toString()

                    def dailyLineNo = 1
                    def dailyHeader = ""
                    ArrayList<UllagePressure> dailyUllagePressures = new ArrayList<>()
                    dailyfile.eachLine { dailyline ->
                        if (dailyLineNo == 1) {
                            dailyHeader = dailyline
                        } else {
                            ArrayList dataElement = dailyline.split(",")
                            UllagePressure dailyUl = new UllagePressure()
                                    .setTankId(Integer.valueOf(dataElement[2]))
                                    .setTimeStamp(Integer.valueOf(dataElement[0]))
                                    .setDdate(dataElement[1])
                                    .setPressure(Double.valueOf(dataElement[3]))

                            dailyUllagePressures.add(dailyUl)
                        }
                        dailyLineNo += 1
                    }
                    // Sort
                    dailyUllagePressures.sort{x,y->
                        if(x.ddate == y.ddate){
                            x.tankId <=> y.tankId
                        }else{
                            x.ddate <=> y.ddate
                        }
                    }
                    StringBuilder dailyStringBuilder = new StringBuilder()
                    UllagePressure dailyUpbefore = new UllagePressure()
                    for (UllagePressure dailyUllagePressure in dailyUllagePressures) {
                        if (dailyUpbefore.toString().equals(dailyUllagePressure.toString())) {
                            dailyUpbefore = dailyUllagePressure.clone()
                            continue
                        }

                        dailyStringBuilder.append(dailyUllagePressure.toString())
                        dailyUpbefore = dailyUllagePressure.clone()
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
