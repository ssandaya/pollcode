/**
 * Created by sandaya on 6/30/2017.
 */


import com.veeder.ava.polldata.DimEvent
import com.veeder.ava.polldata.PollDataUtility
import groovy.io.FileType

class VpsUnsortedGmtFilesDE {

    static void main(String[] args) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo

        String destpath = "C:/cygwin64/home/simulate/proc/polldata/VpsUnsortedData_20171225/" //+ PollDataUtility.toYyyyMmDdDate() + "/"
        String basepath = "C:/cygwin64/home/simulate/proc/polldata/vps_20171225/"  //+ PollDataUtility.toYyyyMmDdDate() + "/"

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

        ArrayList<DimEvent> dimEvents = new ArrayList<>()
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




            if (filename.contains("DimEvents")) {
                println "${filename}   ${dailyfile}"

                lineno = 1
                tempcount++
                file.text.eachLine {
                    line -> /*println  "line : $lineno  $line";*/
                        if ( lineno == 1) {
                            if (!headerSet) {
                                // "TIMESTAMP,DATE,REAL FP,START EVENT,ERROR FLAG,NUMBER OF METERS,METER 1,TRANS VOL 1,TOTAL VOL 1,METER 2,TRANS VOL 2,TOTAL VOL 2"

                                headerLine = "TIMESTAMP,DATE,REAL FP,START EVENT,ERROR FLAG,NUMBER OF METERS,METER 1,TRANS VOL 1,TOTAL VOL 1,METER 2,TRANS VOL 2,TOTAL VOL 2".replaceAll("\"", "").toUpperCase()
                                ArrayList headerarray = headerLine.split(",")
                                headerarray2 = new ArrayList()
                                headerarray2.add(headerarray[0])
                                headerarray2.add(headerarray[1])
                                headerarray2.add(headerarray[2])
                                headerarray2.add(headerarray[3])
                                headerarray2.add(headerarray[4])
                                headerarray2.add(headerarray[5])
                                headerarray2.add(headerarray[6])
                                headerarray2.add(headerarray[7])
                                headerarray2.add(headerarray[8])
                                headerarray2.add(headerarray[9])
                                headerarray2.add(headerarray[10])
                                headerarray2.add(headerarray[11])

                                headerSet = true

                                if (!dailyfile.exists()) {
                                    dailyfile << headerarray2.join(",") + "\n"
                                }

                            }
                        }
                        else {
                            dataLine = line
                            if (!line.empty) {
                                ArrayList dataElement = dataLine.split(",")
                                DimEvent de = new DimEvent()
                                        .setTimeStamp(Integer.valueOf(dataElement[4]))
                                        .setDdate(PollDataUtility.toDdate(new Long(dataElement[4])))
                                        .setFuelPos(Integer.valueOf(dataElement[0]))
                                        .setEventType(Integer.valueOf(dataElement[1]) == 0 ? true : false)
                                        .setErrorFlag(Integer.valueOf(dataElement[3]))
                                        .setNumMeters(Integer.valueOf(dataElement[2]))
                                        .setMeter1(Integer.valueOf(dataElement[6]))
                                        .setTransVol1(Double.valueOf(dataElement[7]))
                                        .setTotalVol1(Double.valueOf(dataElement[8]))
                                        .setMeter2(Integer.valueOf(dataElement[9]))
                                        .setTransVol2(Double.valueOf(dataElement[10]))
                                        .setTotalVol2(Double.valueOf(dataElement[11]))

                                String fn = hdfilename2 + de.getDdate().substring(0, 10).replaceAll("/", "-") + ".csv"
//                                println fn

                                def dailyfile_new = new File(folder, fn)
                                if (dailyfile_new.toString() != dailyfile.toString()) {
                                    if (!dailyfile_new.exists()) {
                                        dailyfile_new << headerarray2.join(",") + "\n"
                                        sleep(1000)
                                    }
                                    dailyfile_new << de.toString()
                                } else {
                                    if (!dailyfile.exists()) {
                                        dailyfile << headerarray2.join(",") + "\n"
                                        sleep(1000)
                                    }
                                    dailyfile << de.toString()

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
