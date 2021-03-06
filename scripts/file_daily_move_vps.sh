#!/bin/bash

SOURCE_DIR="N:/AVA-PollData/"
if [ ! -d "${SOURCE_DIR}" ] ; then
  net use N: \\\\gvrsimvap01\\Share\\N /user:sandaya BdSa998466

  if [ ! -d "${SOURCE_DIR}" ] ; then
    echo "Destination dir ${SOURCE_DIR} not found!"
    exit 1
  else
    echo "Destination dir ${SOURCE_DIR} found!"
  fi
fi


SITE_ID=""
SITE_DESC=""
WIN_SOURCE_DIR="C:/Users/simulate/VPS-PollData/"
WIN_DESTINATION_DIR="C:/cygwin64/home/simulate/proc/polldata/vps_$(date +%Y%m%d)/"
TMP_DESTINATION_DIR="C:/cygwin64/tmp/polldata/vps_$(date +%Y%m%d)/"

GURPS_DIR="/cygdrive/c/Users/simulate/dev/projects/polldata2/exe/"
echo "Gurps Dir: ${GURPS_DIR}"

FINAL_DESTINATION_FILE_BASE="//10.20.95.59/AVA-SiteData/"

mkdir -p ${WIN_DESTINATION_DIR}
if [ ! -d "${WIN_DESTINATION_DIR}" ]; then
   echo "${WIN_DESTINATION_DIR} not created!"
   exit 1
fi

if [ ! -d "$SOURCE_DIR" ] ; then
  echo "${SOURCE_DIR} not found!"
  exit 1
fi  

cd ${WIN_SOURCE_DIR}
pwd
sites="Site*"
echo "Sites: ${sites}"

for dirc in ${sites} 
do
  pwd
  dir=${dirc%*/}
  echo "Dir: ${dir}"
  SITE_ID=${dir:4}

  cd "${dir}"
  for subdir in *
  do 
    pwd
    subdir=${subdir%*/}
    if [[ ${subdir} == *"i@55"* ]]; then
	    SITE_DESC="ProbeSamples"
	 	elif [[ "${subdir}" == *"i@C3"* ]]; then
	    SITE_DESC="DimEvents"
    elif [[ "${subdir}" == *"i&1400"* ]]; then
      SITE_DESC="UllagePressure"
	  fi
    cd "${subdir}"
    date_past=$(date -d "-4 days" +%Y%m%d)
    date_today=$(date +%Y%m%d)

    for ((i = 4;  i >= 0; i--  )) {
      pwd
      gurps_files="${SITE_ID}_${SITE_DESC}_$(date -d "-$i days" +%Y%m%d)*.txt" 

      ls ${gurps_files}
      if [ $? -ne 0 ]; then
        echo "${dir}/${subdir}/${gurps_files} not found!"
        continue
      fi

      for file in ${gurps_files}
      do
        pwd
    	  filer=${file%*/}
        if [ ! -f ${filer} ]; then
          continue
        fi
  	 	  CURRENT_DIR=`pwd`
  	    # echo ${filer}
        SOURCE_FILE=${dir}/${subdir}/${filer}
        GURP_FILE=${WIN_DESTINATION_DIR}${dir}/${subdir}/${SITE_ID}_${SITE_DESC}_${filer:13}
        date_file=${filer:20}
        FINAL_DESTINATION_FILE_PATHNAME=${FINAL_DESTINATION_FILE_BASE}/${SITE_ID}/${SITE_ID}_${SITE_DESC}_${date_part}

        if [ ! -d "${WIN_DESTINATION_DIR}${dir}/${subdir}" ]; then
          mkdir -p "${WIN_DESTINATION_DIR}${dir}/${subdir}"
        fi
        if [ $(grep "UllagePressure" <<< ${filer}) ]; then
          
          # echo "UllagePressure File:-> ${filer}"
          if [ ! -d ${TMP_DESTINATION_DIR}${dir}/${subdir} ]; then
            mkdir -p ${TMP_DESTINATION_DIR}${dir}/${subdir}
          fi
          grep 'i&1400' ${WIN_SOURCE_DIR}${dir}/${subdir}/${filer} > ${TMP_DESTINATION_DIR}${dir}/${subdir}/${filer}
          wait
          sync -f ${TMP_DESTINATION_DIR}${dir}/${subdir}/${filer}
          ${GURPS_DIR}gurps.exe -X=false ${TMP_DESTINATION_DIR}${dir}/${subdir}/${filer}  ${WIN_DESTINATION_DIR}${dir}/${subdir}/${filer}
          wait
          sync -f ${WIN_DESTINATION_DIR}${dir}/${subdir}/${filer}
        else
          # echo ${WIN_DESTINATION_DIR}${dir}/${subdir}/${filer}
          ${GURPS_DIR}gurps.exe -X=false ${WIN_SOURCE_DIR}${dir}/${subdir}/${filer}  ${WIN_DESTINATION_DIR}${dir}/${subdir}/${filer}
          wait
          sync -f ${WIN_DESTINATION_DIR}${dir}/${subdir}/${filer}
        fi
      done
    }
    cd ..
  done
  cd ..
done