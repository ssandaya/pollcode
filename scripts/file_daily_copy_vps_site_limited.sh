#!/bin/bash -vx

if [ $# -eq 0 ]
  then
    echo "No arguments supplied"
    exit 1
fi

if [ -z "$1" ]
  then
    echo "No argument supplied"
    exit 1
fi

SITE_ID=$1

date_today=$(date +%Y%m%d)

DESTINATION_DIR=/cygdrive/z/
DESTINATION_SITE=/cygdrive/q/VpsSortedData_${date_today}/${SITE_ID}
if [ ! -d "${DESTINATION_DIR}" ] ; then
  net use Z: \\\\10.20.95.59\\AVA /user:sandaya xpoint6

  if [ ! -d "${DESTINATION_DIR}" ] ; then
    echo "Destination dir ${DESTINATION_DIR} not found!"
    exit 1
  fi
fi
ls -al /cygdrive/z/AVA-SiteData/${SITE_ID}

cd ${DESTINATION_SITE}
for file in *
do
  if [ ! -d /cygdrive/z/AVA-SiteData/${SITE_ID} ]
  then
    mkdir -p /cygdrive/z/AVA-SiteData/${SITE_ID}
    if [ ! -d /cygdrive/z/AVA-SiteData/${diSITE_IDr} ]
    then
      echo "Not exists: /cygdrive/z/AVA-SiteData/${dir}"
      exit 1
    fi
  fi

 	if [ ! -f /cygdrive/z/AVA-SiteData/${SITE_ID}/${file} ] && [ ! $(grep $(date +%Y-%m-%d) <<< ${file}) ] && [ ! $(grep $(date -d "-7 days" +%Y-%m-%d) <<< ${file}) ]
	then
	  cp -v ${file} /cygdrive/z/AVA-SiteData/${SITE_ID}/${file}
	fi
done

