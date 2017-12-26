#!/bin/bash -vx



SITE_ID=""
SITE_DESC=""
SOURCE_DIR=/cygdrive/n/poll/data/
DESTINATION_DIR=/cygdrive/c/tmp/polldata/
WIN_SOURCE_DIR=n:/poll/data/
WIN_DESTINATION_DIR=x:/AVA-TestData/
GURPS_DIR=c:/Users/sandaya/dev/Gurp/
PROCESSING_DIR= /cygdrive/c/Users/sandaya/dev/tasks/polldata/

cd $SOURCE_DIR

for dir in 0050831045b2 0050831045a0 0050831045a2
do
   dir=${dir%*/}
   echo ${dir##*/}
   if  [ ${dir} == "0050831045b2" ]; then
      echo ${dir}
      SITE_ID="822027"
   elif [ ${dir} == "0050831045a0" ]; then
      echo ${dir}
      SITE_ID="150883"
   elif [ ${dir} == "0050831045a2" ]; then
      echo ${dir}
      SITE_ID="828950"
   fi
   echo ${SITE_ID}

   cd ${dir##*/}

   for subdir in i@T* i@68*
   do 

      subdir=${subdir%*/}
      echo ${subdir##*/}
      if [[ ${subdir} == *"i@T"* ]]; then
	      echo ${dir}
	      SITE_DESC="Meter_Temperature"
	  
	  elif [[ "${subdir}" == *"i@68"* ]]; then
	      echo ${dir}
	      SITE_DESC="Ullage_Pressure"
	  fi
   	  #sleep 15

      cd ${subdir##*/}

      for file in *
      do
		 filer=${file%*/}
		 CURRENT_DIR=`pwd`
		 echo ${filer}
		 #cp ${filer} ${PROCESSING_DIR}input.txt
		 #${GURPS_DIR}Gurps.exe input.txt output.txt
		 #cp output.txt ${DESTINATION_DIR}${SITE_ID}_${SITE_DESC}_${filer:13}

		
       mkdir -p ${WIN_DESTINATION_DIR}${dir}/${subdir}
		 ${GURPS_DIR}gurps.exe  ${WIN_SOURCE_DIR}${dir}/${subdir}/${filer}  ${WIN_DESTINATION_DIR}${dir}/${subdir}/${SITE_ID}_${SITE_DESC}_${filer:13}
       
		 sleep 1
		 #cd ${CURRENT_DIR}
      done
      cd ..
   done

   cd ..
done
