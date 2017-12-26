import java.io.File
import java.text.SimpleDateFormat

class ReadFile {
    static String toDdate(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        return simpleDateFormat.format(new Date((long) timestamp * 1000))
    }

    static void main(String[] args) {
        String basepath = "C:\\Users\\sandaya\\dev\\tasks\\polldata\\"
//        String pathname = basepath + "i@T401\\822027_Meter_Temperature_20170608163300-0000.txt"
//        String pathname2 = basepath + "822027\\822027_Meter_Temperature_" + "2017-06-08.txt"
        String pathname = basepath + "i@6801\\822027_Ullage_Pressure_20170608233500-0000.txt"
        String pathname2 = "C:\\Users\\sandaya\\temp\\822027\\822027_Ullage_Pressure_2017-06-08.txt"
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss")
        String headerLine = ""
        String dataLine = ""
        int tankNo
        int lineno = 1


        println "Before"
        if (pathname.contains("i@T4")) {
            println "i@T4"


            new File(pathname).eachLine {
                line ->
                    println "line : $lineno  $line";
                    if (lineno == 1) headerLine = line.replaceAll("\"", "").toUpperCase()
                    else dataLine += line
                    lineno += 1
            }

            File file = new File(pathname)
            println file.text
            println "The file ${file.absolutePath} has ${file.length()} bytes"

            File file2 = new File(pathname2)

            println "======================================="
            println headerLine
            println dataLine
            println "======================================="

            ArrayList headerarray = headerLine.split(",")
            ArrayList headerarray2 = new ArrayList()
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
            file2 << headerarray2.join(",") + "\n"

            ArrayList dataarray = dataLine.split(",")
            String tankId = dataarray[0].toString().trim()
            println tankId


            dataarray.remove(0);
            for (int i = 0; i < dataarray.size(); i += 8) {
                ArrayList dataElement = new ArrayList()
                for (int j = i; j < 8 + i; j++) {
                    dataElement.add(dataarray[j].toString().trim())

                    if (j == i) {
                        dataElement.add(toDdate(new Long(dataarray[j])))
                        dataElement.add(tankId)
                    }


                    print dataarray[j]

                }
                println ""
                file2 << dataElement.join(",") + "\n"

            }
        } else if (pathname.contains("i@68")) {
            println "i@68"

            new File(pathname).eachLine {
                line ->
                    println "line : $lineno  $line";
                    if (lineno == 1) headerLine = line.replaceAll("\"", "").toUpperCase()
                    else dataLine += (!line.empty?line + "\n":"")
                    lineno += 1
            }

            File file = new File(pathname)
            println file.text
            println "The file ${file.absolutePath} has ${file.length()} bytes"

            File file2 = new File(pathname2)

            println "======================================="
            println headerLine
            println dataLine
            println "======================================="
            ArrayList headerarray = headerLine.split(",")
            ArrayList headerarray2 = new ArrayList()
            headerarray2.add(headerarray[2])
            headerarray2.add("DATE TIME")
            headerarray2.add(headerarray[0])
            headerarray2.add(headerarray[3])

            ArrayList dataarray = new ArrayList()
//            String tankId = dataarray[0]
//            dataarray.remove(0);
            for (line in dataLine.readLines()) {
                ArrayList consarray = new ArrayList()
                ArrayList linearray = line.split(",")
                if (linearray[1]) {
                    consarray.add(linearray[1].toString().trim())
                    consarray.add(toDdate(new Long(linearray[1])))
                    consarray.add(linearray[0].toString().trim())
                    consarray.add(linearray[2].toString().trim())
                    dataarray.add(consarray)
                    println "${linearray} :  ${linearray[1]}"
                } else {
                    println "Line Not OK"
                }

            }
            file2 <<  headerarray2.join(",") + "\n"
            for (ArrayList dat in dataarray ) {
                file2 << dat.join(",") + "\n"
            }

        }

    }
}