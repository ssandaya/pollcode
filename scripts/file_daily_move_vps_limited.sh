#!/bin/bash -x

# SOURCE_DIR="N:/AVA-PollData/"
# if [ ! -d "${SOURCE_DIR}" ] ; then
#   net use N: \\\\gvrsimvap01\\Share\\N /user:sandaya Ssa4260421

#   if [ ! -d "${SOURCE_DIR}" ] ; then
#     echo "Destination dir ${SOURCE_DIR} not found!"
#     exit 1
#   else
#     echo "Destination dir ${SOURCE_DIR} found!"
#   fi
# fi


SITE_ID=""
SITE_DESC=""
WIN_DESTINATION_DIR="\\\\10.20.95.59\\AVA\\VPS-PollData\\"
WIN_SOURCE_DIR_win7LT="\\\\sim-it-return2IT\\VPS-PollData\\"
WIN_SOURCE_DIR_win7TE="\\\\sim-en-bwhit6\\VPS-PollData\\"
WIN_SOURCE_DIR_win7SA="\\\\sim-en-sandaya\\VPS-PollData\\"


if [ ! -d "${WIN_DESTINATION_DIR}" ]; then
  net use H: "${WIN_DESTINATION_DIR}"
  if [ $? -ne 0 ]; then
    echo "${WIN_DESTINATION_DIR} not found!"
    exit 1
  fi
else
   echo "${WIN_DESTINATION_DIR} found!"
fi

for source in ${WIN_SOURCE_DIR_win7TE}
do
  WIN_SOURCE_DIR=${source%*/}
  echo ${WIN_SOURCE_DIR}


  if [ ! -d "${WIN_SOURCE_DIR}" ] ; then
    net use I: "${WIN_SOURCE_DIR}"
    if [ $? -ne 0 ]; then
      echo "${WIN_SOURCE_DIR} not found!"
      continue
    fi
  else   
    echo "${WIN_SOURCE_DIR} found!"
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
      elif [[ "${subdir}" == *"i&14"* ]]; then
        SITE_DESC="UllagePressure"
      elif [[ "${subdir}" == *"i@C3"* ]]; then
        SITE_DESC="DimEvents"
      fi
      echo "subdir: ${subdir} ${SITE_DESC}"
      cd "${subdir}"

      date_past=$(date -d "-0 days" +%Y%m%d%H0000)
      date_today=$(date +%Y%m%d)

      for ((i = 2;  i >= 0; i--  )) {
        pwd
        vps_files="${SITE_ID}_${SITE_DESC}_$(date -d "-$i days" +%Y%m%d)*.txt" 
      
        ls ${vps_files}
        if [ $? -ne 0 ]; then
          continue
        fi

        for file in ${vps_files}
        do
          pwd
      	  filer=${file%*/}
          echo "file: ${filer}"

          filedte_part=${filer##*_}
          echo "filedte_part: ${filedte_part}"

          dte_part=${filedte_part:0:14}
          echo "det_part: ${dte_part}"

          if [ "${dte_part}" -gt "${date_past}" ]; then
            echo "gt"
            continue
          fi
          if [ ! -d "${WIN_DESTINATION_DIR}${dir}/${subdir}" ]; then
            mkdir -p "${WIN_DESTINATION_DIR}${dir}/${subdir}"
          fi
          echo "${filer}" "${WIN_DESTINATION_DIR}${dir}\\${subdir}\\${filer}"
          if [ ! -f "${WIN_DESTINATION_DIR}${dir}\\${subdir}\\${filer}" ]; then
            cp "${filer}"  "${WIN_DESTINATION_DIR}${dir}\\${subdir}\\${filer}"
          fi
        done
      }
      cd ..
    done
    cd ..
  done
done 
