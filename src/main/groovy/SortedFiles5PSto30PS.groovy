/**
 * Created by sandaya on 6/30/2017.
 */
import com.veeder.ava.polldata.PollDataUtility
import com.veeder.ava.polldata.ProbeSample30sec
import com.veeder.ava.polldata.ProbeSample30sec
import com.veeder.ava.polldata.UllagePressure
import groovy.io.FileType

import java.text.SimpleDateFormat

class SortedFiles5PSto30PS {

    static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo

        String destpath = "C:/cygwin64/home/simulate/proc/polldata/SortedData_" + PollDataUtility.toYyyyMmDdDate() + "/"
        String basepath = "C:/cygwin64/home/simulate/proc/polldata/UnsortedData_" + PollDataUtility.toYyyyMmDdDate() + "/"

        def basefolder = new File(basepath)
        if( !basefolder.exists() ) {
            println "Processed gurps ${basepath} files not found"
            return
        }

        def destfolder = new File(destpath)
        if( !destfolder.exists() ) {
            destfolder.mkdirs()
        }

        // array holders for values to be averaged
        def fuelHeights = []
        def waterHeights = []
        def temperature1 = []
        def temperature2 = []
        def temperature3 = []
        def temperature4 = []
        def temperature5 = []
        def temperature6 = []

        int tempTankNo = 0
        String tempBraket30Begin = ""
        String tempBraket30End = ""
        String tempgmttimestamp =""


        // generally used vars
        def count = 0
        def tempcount = 0
        def pressurecount = 0
        def list = []
        def dailyfile0 = new File("", "")
        def dailyfile = new File("", "")
        def dir = new File(basepath)

        ArrayList<UllagePressure> ullagePressures = new ArrayList<>()
        ArrayList<ProbeSample30sec> probeSample30sec = new ArrayList<>()
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
            dailyfile = new File(folder, filename.substring(0,7) + "30secProbeSamples" + "_" + filename_date + ".csv")

            if (filename.contains("5secProbeSamples")) {
                println "${filename}   ${dailyfile}"

                probeSample30sec = new ArrayList<>()

                tempTankNo = 0
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
                            String record_Braket30Begin = PollDataUtility.calculateBegOf30sec(((String)dataElement[1]).replaceAll("/","-"))
                            String record_Braket30End = PollDataUtility.calculateEndOf30sec(((String)dataElement[1]).replaceAll("/","-"))
                            String record_timestamp = record_Braket30Begin.replaceAll("-","/")
                            String record_gmttimestamp = PollDataUtility.toGmtTimestampDate(record_Braket30Begin)
                            int record_tankNo = Integer.valueOf(dataElement[2])

                            if (record_tankNo != tempTankNo || record_Braket30Begin != tempBraket30Begin) {
                                if (tempTankNo != 0 && (!fuelHeights.empty || !waterHeights.empty || !temperature1.empty || !temperature2.empty || !temperature3.empty || !temperature4.empty || !temperature5.empty || !temperature6.empty)) {
                                    // add record
                                    ProbeSample30sec ps = new ProbeSample30sec()
                                            .setTimeStamp(Integer.valueOf(tempgmttimestamp))
                                            .setDdate(tempBraket30Begin.replaceAll("-", "/"))
                                            .setTankId(tempTankNo)
                                            .setFuelHeight(PollDataUtility.avgArrayOfDoubles(fuelHeights, record_timestamp))
                                            .setWaterHeight(PollDataUtility.avgArrayOfDoubles(waterHeights, record_timestamp))
                                            .setTemperatures(new ArrayList<Double>([PollDataUtility.avgArrayOfDoubles(temperature1, record_timestamp), PollDataUtility.avgArrayOfDoubles(temperature2, record_timestamp), PollDataUtility.avgArrayOfDoubles(temperature3, record_timestamp), PollDataUtility.avgArrayOfDoubles(temperature4, record_timestamp), PollDataUtility.avgArrayOfDoubles(temperature5 , record_timestamp), PollDataUtility.avgArrayOfDoubles(temperature6, record_timestamp)]))

                                    probeSample30sec.add(ps)
                                }
                                // init
                                tempBraket30Begin = record_Braket30Begin
                                tempTankNo = record_tankNo
                                tempgmttimestamp = record_gmttimestamp

                                fuelHeights  = []
                                waterHeights = []
                                temperature1 = []
                                temperature2 = []
                                temperature3 = []
                                temperature4 = []
                                temperature5 = []
                                temperature6 = []

                                fuelHeights  << dataElement[3]
                                waterHeights << dataElement[4]
                                temperature1 << dataElement[5]
                                temperature2 << dataElement[6]
                                temperature3 << dataElement[7]
                                temperature4 << dataElement[8]
                                temperature5 << dataElement[9]
                                temperature6 << dataElement[10]

                            } else {
                                fuelHeights  << dataElement[3]
                                waterHeights << dataElement[4]
                                temperature1 << dataElement[5]
                                temperature2 << dataElement[6]
                                temperature3 << dataElement[7]
                                temperature4 << dataElement[8]
                                temperature5 << dataElement[9]
                                temperature6 << dataElement[10]
                            }



                        }
                        lineno += 1
                }
                // Sort
                probeSample30sec.sort{x,y->
                    if(x.ddate == y.ddate){
                        x.tankId <=> y.tankId
                    }else{
                        x.ddate <=> y.ddate
                    }
                }
                StringBuilder stringBuilder = new StringBuilder()
                ProbeSample30sec mtbefore = new ProbeSample30sec()
                for (ProbeSample30sec ps5 in probeSample30sec) {
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
                    ArrayList<ProbeSample30sec> dailyProbeSample30sec = new ArrayList<>()
                    dailyfile.eachLine { dailyline ->
                        if (dailyLineNo == 1) {
                            dailyHeader = dailyline
                        } else {
                            ArrayList<String> dataElement = dailyline.split(",")
                            ProbeSample30sec ps = new ProbeSample30sec()
                                    .setTimeStamp(Integer.valueOf(dataElement[0]))
                                    .setDdate(dataElement[1])
                                    .setTankId(Integer.valueOf(dataElement[2]))
                                    .setFuelHeight(Double.valueOf(dataElement[3]))
                                    .setWaterHeight(Double.valueOf(dataElement[4]))
                                    .setTemperatures(new ArrayList<Double>([dataElement[5], dataElement[6], dataElement[7], dataElement[8], dataElement[9], dataElement[10]]))

                            dailyProbeSample30sec.add(ps)

                        }
                        dailyLineNo += 1
                    }
                    // Sort
                    dailyProbeSample30sec.sort{x,y->
                        if(x.ddate == y.ddate){
                            x.tankId <=> y.tankId
                        }else{
                            x.ddate <=> y.ddate
                        }
                    }

                    StringBuilder dailyStringBuilder = new StringBuilder()
                    ProbeSample30sec daily5Psbefore = new ProbeSample30sec()
                    for (ProbeSample30sec daily5Ps in dailyProbeSample30sec) {
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
