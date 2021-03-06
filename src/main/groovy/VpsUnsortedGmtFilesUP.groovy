/**
 * Created by sandaya on 6/30/2017.
 */


import com.veeder.ava.polldata.MeterTemperature
import com.veeder.ava.polldata.PollDataUtility
import com.veeder.ava.polldata.UllagePressure
import groovy.io.FileType

class VpsUnsortedGmtFilesUP {

    static void main(String[] args) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo

        String destpath = "C:/cygwin64/home/simulate/proc/polldata/VpsUnsortedData_" + PollDataUtility.toYyyyMmDdDate() + "/"
        String basepath = "C:/cygwin64/home/simulate/proc/polldata/vps_" + PollDataUtility.toYyyyMmDdDate() + "/"

        def basefolder = new File(basepath)
        if( !basefolder.exists() ) {
            println basepath
            println "Processed vps ${basepath} files not found"
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




            if (filename.contains("UllagePressure")) {
                println "${filename}   ${dailyfile}"

                lineno = 1
                pressurecount++
                dataLine = ""
                file.text.eachLine {
                    line -> /*println  "line : $lineno  $line";*/
                        if ( lineno == 1 ) headerLine = line.replaceAll("\"","").toUpperCase()
                        else dataLine += (!line.empty?line + "\n":"")
                        lineno += 1
                }

                ArrayList headerarray = headerLine.split(",")
                headerarray2 = new ArrayList()
                //TIMESTAMP,DATE TIME,SENSORID,PRESSURE
                headerarray2.add("TIMESTAMP")
                headerarray2.add("DATE TIME")
                headerarray2.add("SENSORID")
                headerarray2.add("PRESSURE")

                ArrayList dataarray = new ArrayList()

                for (line in dataLine.readLines()) {
                    ArrayList consarray = new ArrayList()
                    ArrayList linearray = line.split(",")

//                    println linearray
                    boolean bNull = false
                    for (int i=0; i<=2; i++) {
                        def el = linearray[i]
                        if (linearray[i] == null || linearray[i] == "null") {
                            bNull = true
                        }
                    }
                    if (!bNull) {
                        if (linearray[1]) {
                            consarray.add(linearray[1].toString().trim())
                            consarray.add(PollDataUtility.toDdate(new Long(linearray[1])))
                            consarray.add("1")
                            consarray.add(linearray[2].toString().trim())
                            dataarray.add(consarray)
//                        println "${linearray} :  ${linearray[1]}"
                        } else {
//                        println "Line Not OK"
                        }
                    }
                }



                for (ArrayList dat in dataarray ) {
                    String fn = hdfilename2 + dat[1].toString().substring(0,10).replaceAll("/","-") + ".csv"

                    def dailyfile_new = new File(folder, fn)
                    if (dailyfile_new.toString() != dailyfile.toString()) {
                        if (!dailyfile_new.exists()) {
                            dailyfile_new << headerarray2.join(",") + "\n"
                            sleep(1000)
                        }
                        dailyfile_new << dat.join(",") + "\n"
                    } else {
                        if (!dailyfile.exists()) {
                            dailyfile << headerarray2.join(",") + "\n"
                            sleep(1000)
                        }
                        dailyfile << dat.join(",") + "\n"
                    }

                }

            }

            count++
        }

        println "Total Files: ${count; } Total Tempfiles: ${tempcount} Total Pressurefiles: ${pressurecount}"
    }
}
