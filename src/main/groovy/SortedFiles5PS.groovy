/**
 * Created by sandaya on 6/30/2017.
 */

import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.PollDataUtility
import com.veeder.ava.polldata.ProbeSample5sec
import com.veeder.ava.polldata.UllagePressure
import groovy.io.FileType

import java.text.DateFormat
import java.text.SimpleDateFormat

class SortedFiles5PS {

    static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo

        String destpath = "C:/cygwin64/home/simulate/proc/polldata/SortedData_000001B_20180201/" //+ PollDataUtility.toYyyyMmDdDate() + "/"
        String basepath = "C:/cygwin64/home/simulate/proc/polldata/UnsortedData_000001B_20180201/" //+ PollDataUtility.toYyyyMmDdDate() + "/"


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
        ArrayList<ProbeSample5sec> probeSample5sec = new ArrayList<>()
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
            if (filename.contains("5secProbeSamples")) {
                println "${filename}   ${dailyfile}"

                probeSample5sec = new ArrayList<>()

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
                            ProbeSample5sec ps = new ProbeSample5sec()
                                    .setTimeStamp(Integer.valueOf(dataElement[0]))
                                    .setDdate(dataElement[1])
                                    .setTankId(Integer.valueOf(dataElement[2]))
                                    .setFuelHeight(Double.valueOf(dataElement[3]))
                                    .setWaterHeight(Double.valueOf(dataElement[4]))
                                    .setTemperatures(new ArrayList<Double>([dataElement[5], dataElement[6], dataElement[7], dataElement[8], dataElement[9], dataElement[10]]))

                            probeSample5sec.add(ps)
                        }
                        lineno += 1
                }
                // Sort
                probeSample5sec.sort{x,y->
                    if(x.ddate == y.ddate){
                        x.tankId <=> y.tankId
                    }else{
                        x.ddate <=> y.ddate
                    }
                }
                StringBuilder stringBuilder = new StringBuilder()
                ProbeSample5sec mtbefore = new ProbeSample5sec()
                for (ProbeSample5sec ps5 in probeSample5sec) {
                    if (mtbefore.toString().equals(ps5.toString())) {
                        mtbefore = ps5.clone()
                        continue
                    }

                    stringBuilder.append(ps5.toString())
                    mtbefore = ps5.clone()
                }
                // check if merge is needed
                if (dailyfile.exists()) {
                    dailyfile << stringBuilder.toString()

                    def dailyLineNo = 1
                    def dailyHeader = ""
                    ArrayList<ProbeSample5sec> dailyProbeSample5sec = new ArrayList<>()
                    dailyfile.eachLine { dailyline ->
                        if (dailyLineNo == 1) {
                            dailyHeader = dailyline
                        } else {
                            ArrayList<String> dataElement = dailyline.split(",")
                            ProbeSample5sec ps = new ProbeSample5sec()
                                    .setTimeStamp(Integer.valueOf(dataElement[0]))
                                    .setDdate(dataElement[1])
                                    .setTankId(Integer.valueOf(dataElement[2]))
                                    .setFuelHeight(Double.valueOf(dataElement[3]))
                                    .setWaterHeight(Double.valueOf(dataElement[4]))
                                    .setTemperatures(new ArrayList<Double>([dataElement[5], dataElement[6], dataElement[7], dataElement[8], dataElement[9], dataElement[10]]))

                            dailyProbeSample5sec.add(ps)

                        }
                        dailyLineNo += 1
                    }
                    // Sort
                    dailyProbeSample5sec.sort{x,y->
                        if(x.ddate == y.ddate){
                            x.tankId <=> y.tankId
                        }else{
                            x.ddate <=> y.ddate
                        }
                    }

                    StringBuilder dailyStringBuilder = new StringBuilder()
                    ProbeSample5sec daily5Psbefore = new ProbeSample5sec()
                    for (ProbeSample5sec daily5Ps in dailyProbeSample5sec) {
                        if (daily5Psbefore.toString().equals(daily5Ps.toString())) {
                            daily5Psbefore = daily5Ps.clone()
                            continue
                        }
                        dailyStringBuilder.append(daily5Ps.toString())
                        daily5Psbefore = daily5Ps.clone()
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
