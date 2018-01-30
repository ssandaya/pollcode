#!/bin/bash -vx

date_today=$(date +%Y%m%d)

DESTINATION_DIR="\\\\10.20.95.59\\AVA\\"
DESTINATION_SITE=/cygdrive/c/cygwin64/home/simulate/proc/polldata/VpsSortedData_${date_today}
if [ ! -d "${DESTINATION_DIR}" ] ; then
  net use W: \\\\10.20.95.59\\AVA /user:sandaya xpoint6

  if [ ! -d "${DESTINATION_DIR}" ] ; then
    echo "Destination dir ${DESTINATION_DIR} not found!"
    exit 1
  fi
fi
ls -al /cygdrive/w/AVA-SiteData

cd ${DESTINATION_SITE}
for dirc in *
do
  dir=${dirc%*/}
  SITE_ID=${dir}

  cd ${dir}
  for file in *
  do
    if [ ! -d /cygdrive/w/AVA-SiteData/${dir} ]
    then
      mkdir -p /cygdrive/w/AVA-SiteData/${dir}
      if [ ! -d /cygdrive/w/AVA-SiteData/${dir} ]
      then
        echo "Not exists: /cygdrive/w/AVA-SiteData/${dir}"
        exit 1
      fi
    fi

   	if [ ! -f /cygdrive/w/AVA-SiteData/${dir}/${file} ] && [ ! $(grep $(date +%Y-%m-%d) <<< ${file}) ] && [ ! $(grep $(date -d "-7 days" +%Y-%m-%d) <<< ${file}) ]
  	then
  	  cp -v ${file} /cygdrive/w/AVA-SiteData/${dir}/${file}
  	fi
  done
  cd ..
done

# net use W: /delete /y