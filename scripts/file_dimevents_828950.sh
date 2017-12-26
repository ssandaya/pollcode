#!/bin/bash -vx

SOURCE_DIR="N:/poll/data/"
if [ ! -d "${SOURCE_DIR}" ] ; then
  net use N: \\\\gvrsimvap01\\Share\\N /user:sandaya Fye4260421

  if [ ! -d "${SOURCE_DIR}" ] ; then
    echo "Destination dir ${SOURCE_DIR} not found!"
    exit 1
  else
    echo "Destination dir ${SOURCE_DIR} found!"
  fi
fi


SITE_ID=""
SITE_DESC=""
DESTINATION_DIR="/cygdrive/c/tmp/polldata/"
WIN_SOURCE_DIR="N:/poll/data/"
WIN_DESTINATION_DIR="c:/cygwin64/home/simulate/proc/polldata/dimevents828950_$(date +%Y%m%d)/"

GURPS_DIR="/cygdrive/c/Users/simulate/dev/projects/polldata2/exe/"
PROCESSING_DIR="/cygdrive/c/cygwin64/home/simulate/proc/proclogs/"

# FINAL_DESTINATION_FILE_BASE=X:/AVA-SiteData
FINAL_DESTINATION_FILE_BASE=C:/cygwin64/home/simulate/proc/polldata/AVA-SiteData

countMt=0

if [ ! -d "$SOURCE_DIR" ] ; then
  echo "${SOURCE_DIR} not found!"
  exit 1
fi  
cd "$SOURCE_DIR"
ls -al 0050831045a2

mkdir -p ${WIN_DESTINATION_DIR}
if [ ! -d "${WIN_DESTINATION_DIR}" ]; then
   echo "${WIN_DESTINATION_DIR} not created!"
   exit 1
fi

for dir in 0050831045a2
do
  dir=${dir%*/}
  echo ${dir##*/}
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
  echo ${SITE_ID}

  cd ${dir##*/}
  for subdir in i@AM00
  do 
    subdir=${subdir%*/}
    # echo ${subdir##*/}
    if [[ ${subdir} == *"i@T"* ]]; then
	    # echo ${dir}
	    SITE_DESC="MeterTemperature"
	 	elif [[ "${subdir}" == *"i@68"* ]]; then
	    # echo ${dir}
	    SITE_DESC="UllagePressure"
    elif [[ "${subdir}" == *"i@67"* ]]; then
      # echo ${dir}
      SITE_DESC="5secProbeSamples"
    elif [[ "${subdir}" == *"i@AM00"* ]]; then
      # echo ${dir}
      SITE_DESC="DimEvents"

	  fi
    cd "${subdir##*/}"
    date_past=$(date -d "-42 days" +%Y%m%d)
    date_today=$(date +%Y%m%d)
    echo ${date_past}
    echo ${date_today}
    for ((i = 42;  i >= 27; i--  )) {
      echo "${dir}-$(date -d "-$i days" +%Y%m%d)*.txt"  
      gurps_files="${dir}-$(date -d "-$i days" +%Y%m%d)*.txt" 
    
      
      for file in ${gurps_files}
      do
        filer=${file%*/}
        CURRENT_DIR=`pwd`
        # echo ${filer}
        SOURCE_FILE=${dir}/${subdir}/${filer}
        echo "source file: ${SOURCE_FILE}"

        GURP_FILE=${WIN_DESTINATION_DIR}${dir}/${subdir}/${SITE_ID}_${SITE_DESC}_${filer:13}
        date_file=${filer:13:8}
        date_part="${date_file:0:4}-${date_file:4:2}-${date_file:6:2}.csv"
        FINAL_DESTINATION_FILE_PATHNAME=${FINAL_DESTINATION_FILE_BASE}/${SITE_ID}/${SITE_ID}_${SITE_DESC}_${date_part}
        # if [ $date_file -gt $date_past ]  && [ ! -f $FINAL_DESTINATION_FILE_PATHNAME ]; then
          echo "${WIN_SOURCE_DIR}${dir}/${subdir}/${filer} -> ${WIN_DESTINATION_DIR}${dir}/${subdir}/${SITE_ID}_${SITE_DESC}_${filer:13}" 
          mkdir -p ${WIN_DESTINATION_DIR}${dir}/${subdir}
          if [ ! -d "${WIN_DESTINATION_DIR}${dir}/${subdir}" ]; then
             echo "${WIN_DESTINATION_DIR}${dir}/${subdir} not created!"
             exit 1
          fi
          ${GURPS_DIR}gurps.exe  ${WIN_SOURCE_DIR}${dir}/${subdir}/${filer}  ${WIN_DESTINATION_DIR}${dir}/${subdir}/${SITE_ID}_${SITE_DESC}_${filer:13}
        # fi
      done 
    }
    cd ..
  done
  cd ..
done