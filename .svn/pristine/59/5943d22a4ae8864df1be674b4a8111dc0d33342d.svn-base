#!/bin/bash 

SITE_ID=""
SITE_DESC=""
SOURCE_DIR=n:/poll/data/
DESTINATION_DIR=c:/tmp/polldata/
WIN_SOURCE_DIR=n:/poll/data/
WIN_DESTINATION_DIR=c:/cygwin64/home/simulate/proc/polldata/gurpsfiles/
GURPS_DIR=c:/Users/simulate/dev/projects/polldata2/exe/
PROCESSING_DIR=c:/cygwin64/home/simulate/proc/proclogs/

# FINAL_DESTINATION_FILE_BASE=X:/AVA-SiteData
FINAL_DESTINATION_FILE_BASE=C:/cygwin64/home/simulate/proc/polldata/AVA-SiteData

countMt=0
# countMt=`expr $countMt + 1`


cd "$SOURCE_DIR"

for dir in 0050831045b2 0050831045a0 0050831045a2
do
   dir=${dir%*/}
   # echo ${dir##*/}
   if  [ ${dir} == "0050831045b2" ]; then
      # echo ${dir}
      SITE_ID="822027"
    elif [ ${dir} == "0050831045a0" ]; then
       # echo ${dir}
       SITE_ID="150883"
    elif [ ${dir} == "0050831045a2" ]; then
       # echo ${dir}
       SITE_ID="828950"
   fi
   # echo ${SITE_ID}

   cd "${dir##*/}"

   for subdir in i@68*
   do 

      subdir=${subdir%*/}
      # echo ${subdir##*/}
      if [[ ${subdir} == *"i@T"* ]]; then
	      # echo ${dir}
	      SITE_DESC="Meter_Temperature"
	  
	   elif [[ "${subdir}" == *"i@68"* ]]; then
	       # echo ${dir}
	       SITE_DESC="Ullage_Pressure"
	   fi

      cd "${subdir##*/}"

      for file in *
      do
		  filer=${file%*/}
		  CURRENT_DIR=`pwd`
		  # echo ${filer}

        SOURCE_FILE=${dir}/${subdir}/${filer}
        GURP_FILE=${WIN_DESTINATION_DIR}${dir}/${subdir}/${SITE_ID}_${SITE_DESC}_${filer:13}
        echo "$SOURCE_FILE -> $GURP_FILE"  >> "C:/cygwin64/home/simulate/proc/proclogs/gurps_${dir}_${subdir}-${SITE_ID}.log"
        # echo  "raw_name : ${WIN_DESTINATION_DIR}${dir}/${subdir}/${SITE_ID}_${SITE_DESC}_${filer:13}"
        # echo  "gurp_file: $GURP_FILE"


        date_past=$(date -d "-16 days" +%Y%m%d)
        date_today=$(date +%Y%m%d)
        # echo "$date_file $date_past $date_today"

        date_file=${filer:13:8}
        # echo $date_file
        # date filter and file exist filter
        date_part="${date_file:0:4}-${date_file:4:2}-${date_file:6:2}.csv"

        FINAL_DESTINATION_FILE_PATHNAME=${FINAL_DESTINATION_FILE_BASE}/${SITE_ID}/${SITE_ID}_${SITE_DESC}_${date_part}
        # echo "Final Dest: $FINAL_DESTINATION_FILE_PATHNAME"

        # sleep 10

        # if [ $date_file -gt $date_past ] && [ $date_file -ne $date_today ] && [ ! -f $FINAL_DESTINATION_FILE_PATHNAME ]; then
          # echo "process"


   		 #cp ${filer} ${PROCESSING_DIR}input.txt
   		 #${GURPS_DIR}Gurps.exe input.txt output.txt
   		 #cp output.txt ${DESTINATION_DIR}${SITE_ID}_${SITE_DESC}_${filer:13}

          # echo "gurps.exe -> ${WIN_SOURCE_DIR}${dir}/${subdir}/${filer}  ${WIN_DESTINATION_DIR}${dir}/${subdir}/${SITE_ID}_${SITE_DESC}_${filer:13}"
          # sleep 15
   		
          # 2 Proc lines
          mkdir -p ${WIN_DESTINATION_DIR}${dir}/${subdir}
   		 ${GURPS_DIR}gurps.exe  ${WIN_SOURCE_DIR}${dir}/${subdir}/${filer}  ${WIN_DESTINATION_DIR}${dir}/${subdir}/${SITE_ID}_${SITE_DESC}_${filer:13}
          
   		 # sleep 1
   		 #cd ${CURRENT_DIR}
        # fi

         # sleep 20
         done

         cd ..
      done
   
   cd ..
done
