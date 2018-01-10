#!/bin/bash

date_today=$(date +%Y%m%d)

DESTINATION_DIR=Y:/
DESTINATION_SITE=/cygdrive/c/cygwin64/home/simulate/proc/polldata/VpsSortedData_${date_today}
if [ ! -d "${DESTINATION_DIR}" ] ; then
  net use Y: \\\\10.20.95.59\\AVA /user:sandaya xpoint6

  if [ ! -d "${DESTINATION_DIR}" ] ; then
    echo "Destination dir ${DESTINATION_DIR} not found!"
    exit 1
  fi
fi
ls -al Y:/AVA-SiteData

cd ${DESTINATION_SITE}
for dirc in *
do
  dir=${dirc%*/}
#  echo "Dir: ${dir}"
  # echo ${dir}
  SITE_ID=${dir}

  cd ${dir}
  for file in *
  do
#    echo "file: ${file}"
    mkdir -p /cygdrive/y/AVA-SiteData/${dir}
    if [ ! -d /cygdrive/y/AVA-SiteData/${dir} ]
    then
       echo "Not exists: /cygdrive/y/AVA-SiteData/${dir}"
       exit 1
    fi

   	if [ ! -f /cygdrive/y/AVA-SiteData/${dir}/${file} ] && [ ! $(grep $(date +%Y-%m-%d) <<< ${file}) ] && [ ! $(grep $(date -d "-5 days" +%Y-%m-%d) <<< ${file}) ]
  	then
#  	  echo ${file} /cygdrive/y/AVA-SiteData/${dir}/${file}
  	  cp -v ${file} /cygdrive/y/AVA-SiteData/${dir}/${file}
  	fi
  done
  cd ..
done

net use Y: /delete /y