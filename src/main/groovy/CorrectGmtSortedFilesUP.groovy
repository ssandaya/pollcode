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

class CorrectGmtSortedFilesUP {


    static String toYyyyMmDdDate() {
        DateFormat gmtFormat = new SimpleDateFormat("yyyyMMdd")
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return gmtFormat.format(new Date());
    }

    static String toYyyy_Mm_DdDate() {
        DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd")
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return gmtFormat.format(new Date());
    }

    static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo

        String destpath = "C:/cygwin64/home/simulate/proc/polldata_gmtsorted/"
        String basepath = "C:/cygwin64/home/simulate/proc/polldata_gmtcorrected/"

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
                        if ( lineno == 1) {
                            if (!headerSet) {
                                headerLine = line.replaceAll("\"", "").toUpperCase()
                                headerarray = headerLine.split(",")

                                headerSet = true

                            }
                        }
                        else {
                            dataLine = line

                            ArrayList dataElement = dataLine.split(",")
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
                UllagePressure mtbefore = new UllagePressure()
                for (UllagePressure up in ullagePressures) {
                    if (mtbefore.toString().equals(up.toString())) {
                        mtbefore = up.clone()
                        continue
                    }

                    stringBuilder.append(up.toString())
                    mtbefore = up.clone()
                }
                // check if merge is needed
                if (dailyfile.exists()) {
                    dailyfile << stringBuilder.toString()

                    def dailyLineNo = 1
                    def dailyHeader = ""
                    ArrayList<UllagePressure> dailyUllagePressure = new ArrayList<>()
                    dailyfile.eachLine { dailyline ->
                        if (dailyLineNo == 1) {
                            dailyHeader = dailyline
                        } else {
                            ArrayList<String> dataElement = dailyline.split(",")
                            UllagePressure ul = new UllagePressure()
                                    .setTankId(Integer.valueOf(dataElement[2]))
                                    .setTimeStamp(Integer.valueOf(dataElement[0]))
                                    .setDdate(dataElement[1])
                                    .setPressure(Double.valueOf(dataElement[3]))

                            dailyUllagePressure.add(ul)

                        }
                        dailyLineNo += 1
                    }
                    // Sort
                    dailyUllagePressure.sort{x,y->
                        if(x.ddate == y.ddate){
                            x.tankId <=> y.tankId
                        }else{
                            x.ddate <=> y.ddate
                        }
                    }

                    StringBuilder dailyStringBuilder = new StringBuilder()
                    UllagePressure dailyUPbefore = new UllagePressure()
                    for (UllagePressure up in dailyUllagePressure) {
                        if (dailyUPbefore.toString().equals(up.toString())) {
                            dailyUPbefore = up.clone()
                            continue
                        }
                        dailyStringBuilder.append(up.toString())
                        dailyUPbefore = up.clone()
                    }
                    dailyfile.delete()
                    dailyfile << dailyHeader
                    dailyfile << dailyStringBuilder.toString()
                } else {
                    if (!dailyfile.name.contains(toYyyy_Mm_DdDate())) {
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
