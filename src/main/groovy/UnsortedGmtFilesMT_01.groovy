/**
 * Created by sandaya on 6/30/2017.
 */


import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.PollDataUtility
import com.veeder.ava.polldata.UllagePressure
import groovy.io.FileType

class UnsortedGmtFilesMT_01 {

    static void main(String[] args) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo

        String destpath = "C:/cygwin64/home/simulate/proc/polldata/UnsortedData_R20171225/" //+ PollDataUtility.toYyyyMmDdDate() + "/"
        String basepath = "C:/cygwin64/home/simulate/proc/polldata/gurps_R20171225/" //+ PollDataUtility.toYyyyMmDdDate() + "/"

        def basefolder = new File(basepath)
        if( !basefolder.exists() ) {
            println basepath
            println "Processed gurps files not found"
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
            int tankId = 0

            if (filename.contains("MeterTemperature")) {
                println "${filename}   ${dailyfile}"

                lineno = 1
                tempcount++
                file.text.eachLine {
                    line -> /*println  "line : $lineno  $line";*/
                        if (!line.trim().empty) {
                            if (lineno == 1) {
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
                            } else if (lineno == 2) {
                                dataLine = line
                                ArrayList dataarray0 = dataLine.split(",")
                                tankId = Integer.valueOf(dataarray0.head())
                            } else {
                                dataLine = line
                                ArrayList dataElement = dataLine.split(",")
//                            for (int i = 0; i < dataarray.size(); i += 8) {
//                                ArrayList dataElement = dataarray.subList(i,i+8)
                                MeterTemperature mt = new MeterTemperature()
                                        .setTankId(tankId)
                                        .setTimeStamp(Integer.valueOf(dataElement[0]))
                                        .setDdate(PollDataUtility.toDdate(new Long(dataElement[0])))
                                        .setTemperatures(new ArrayList<Double>([dataElement[1].toString().trim(), dataElement[2].toString().trim(), dataElement[3].toString().trim(), dataElement[4].toString().trim(), dataElement[5].toString().trim(), dataElement[6].toString().trim(), dataElement[7].toString().trim()]))

                                String fn = hdfilename2 + mt.getDdate().substring(0, 10).replaceAll("/", "-") + ".csv"
//                                println fn

                                def dailyfile_new = new File(folder, fn)
                                if (dailyfile_new.toString() != dailyfile.toString()) {
                                    if (!dailyfile_new.exists()) {
                                        dailyfile_new << headerarray2.join(",") + "\n"
//                                        sleep(10)
                                    }
                                    dailyfile_new << mt.toString()
                                } else {
                                    if (!dailyfile.exists()) {
                                        dailyfile << headerarray2.join(",") + "\n"
//                                        sleep(10)
                                    }
                                    dailyfile << mt.toString()

//                                }
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
