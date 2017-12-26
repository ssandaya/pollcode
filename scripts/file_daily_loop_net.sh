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
WIN_SOURCE_DIR="N:/AVA-PollData/"
WIN_DESTINATION_DIR="C:/cygwin64/home/simulate/proc/polldata/vps_$(date +%Y%m%d)/"

GURPS_DIR="/cygdrive/c/Users/simulate/dev/projects/polldata2/exe/"
echo "Gurps Dir: ${GURPS_DIR}"

# FINAL_DESTINATION_FILE_BASE=X:/AVA-SiteData
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

cd /cygdrive/n/AVA-PollData/
pwd
sites="Site*"
echo "Site"

echo "Sites: ${sites}"

for dirc in ${sites} 
do
  pwd
  dir=${dirc%*/}
  echo "Dir: ${dir}"
  # echo ${dir}
  SITE_ID=${dir:4}

  # echo "Site ID: ${dir} ${SITE_ID}"


  cd "${dir}"
  for subdir in *
  do 
    pwd
    subdir=${subdir%*/}
    # echo ${subdir##*/}
    if [[ ${subdir} == *"i@55"* ]]; then
	    # echo ${dir}
	    SITE_DESC="ProbeSamples"
	 	elif [[ "${subdir}" == *"i@C3"* ]]; then
	    # echo ${dir}
	    SITE_DESC="DimEvents"
	  fi
    cd "${subdir}"
    date_past=$(date -d "-7 days" +%Y%m%d)
    date_today=$(date +%Y%m%d)
    echo "Dir/Subdir: ${dir}/${subdir}"

    for ((i = 6;  i >= 0; i--  )) {
      pwd
      # echo "${dir}-$(date -d "-$i days" +%Y%m%d)*.txt"  
      gurps_files="${SITE_ID}-${SITE_DESC}-$(date -d "-$i days" +%Y%m%d)*.txt" 
      echo "GurpsFiles: ${gurps_files}"

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
        date_part="${date_file:0:4}-${date_file:4:2}-${date_file:6:2}.csv"
        FINAL_DESTINATION_FILE_PATHNAME=${FINAL_DESTINATION_FILE_BASE}/${SITE_ID}/${SITE_ID}_${SITE_DESC}_${date_part}

        # if [ ! -f $FINAL_DESTINATION_FILE_PATHNAME ]; then
          echo "${WIN_SOURCE_DIR}${dir}/${subdir}/${filer} -> ${WIN_DESTINATION_DIR}${dir}/${subdir}/${SITE_ID}_${SITE_DESC}_${date_part}" 
          mkdir -p "${WIN_DESTINATION_DIR}${dir}/${subdir}"
          if [ ! -d "${WIN_DESTINATION_DIR}${dir}/${subdir}" ]; then
             echo "${WIN_DESTINATION_DIR}${dir}/${subdir} not created!"
             exit 1
          fi
    	    ${GURPS_DIR}gurps.exe  ${WIN_SOURCE_DIR}${dir}/${subdir}/${filer}  ${WIN_DESTINATION_DIR}${dir}/${subdir}/${filer}
        # fi
      done
    }
    cd ..
  done
  cd ..
done