#!/bin/bash -vx

date_today=$(date -d "-1 days" +%Y%m%d)
DESTINATION_DIR=Y:/
DESTINATION_SITE_150883=/cygdrive/c/cygwin64/home/simulate/proc/polldata/SortedData_${date_today}/150883
DESTINATION_SITE_822027=/cygdrive/c/cygwin64/home/simulate/proc/polldata/SortedData_${date_today}/822027
DESTINATION_SITE_828950=/cygdrive/c/cygwin64/home/simulate/proc/polldata/SortedData_${date_today}/828950
DESTINATION_SITE_843444=/cygdrive/c/cygwin64/home/simulate/proc/polldata/SortedData_${date_today}/843444
DESTINATION_SITE_000001=/cygdrive/c/cygwin64/home/simulate/proc/polldata/SortedData_000001_20180201/000001

if [ ! -d "${DESTINATION_DIR}" ] ; then
  net use Y: \\\\10.20.95.59\\AVA /user:sandaya xpoint6

  if [ ! -d "${DESTINATION_DIR}" ] ; then
    echo "Destination dir ${DESTINATION_DIR} not found!"
    exit 1
  fi
fi
ls -al Y:/AVA-SiteData

if [ -d ${DESTINATION_SITE_000001} ]; then
  cd ${DESTINATION_SITE_000001}
  for file in *
  do
  	# echo ${file}
   	if [ ! -f /cygdrive/y/AVA-SiteData/000001/${file} ] && [ ! $(grep $(date -d "-1 days" +%Y-%m-%d) <<< ${file}) ] && [ ! $(grep $(date -d "-7 days" +%Y-%m-%d) <<< ${file}) ]
  	then
  	  cp -v ${file} /cygdrive/y/AVA-SiteData/000001/${file}
  	  # echo ${file} /cygdrive/y/AVA-SiteData/000001/${file}
  	fi
  done
fi

exit 0

if [ -d ${DESTINATION_SITE_822027} ]; then	
  cd ${DESTINATION_SITE_822027} 
  for file in *
  do
  	# echo ${file}
   	if [ ! -f /cygdrive/y/AVA-SiteData/822027/${file} ] && [ ! $(grep $(date -d "-1 days" +%Y-%m-%d) <<< ${file}) ] && [ ! $(grep $(date -d "-8 days" +%Y-%m-%d) <<< ${file}) ]
   	then
  	  cp -v ${file} /cygdrive/y/AVA-SiteData/822027/${file}
  	  # echo ${file} /cygdrive/y/AVA-SiteData/822027/${file}
  	fi
  done
fi

if [ -d ${DESTINATION_SITE_828950} ]; then	
  cd ${DESTINATION_SITE_828950}
  for file in *
  do
  	# echo ${file}
   	if [ ! -f /cygdrive/y/AVA-SiteData/828950/${file} ] && [ ! $(grep $(date -d "-1 days" +%Y-%m-%d) <<< ${file}) ] && [ ! $(grep $(date -d "-8 days" +%Y-%m-%d) <<< ${file}) ]
   	then
  	  cp -v ${file} /cygdrive/y/AVA-SiteData/828950/${file}
  	  # echo ${file} /cygdrive/y/AVA-SiteData/828950/${file}
  	fi
  done
fi

if [ -d ${DESTINATION_SITE_843444} ]; then  
  cd ${DESTINATION_SITE_843444}
  for file in *
  do
    # echo ${file}
    if [ ! -f /cygdrive/y/AVA-SiteData/843444/${file} ] && [ ! $(grep $(date -d "-1 days" +%Y-%m-%d) <<< ${file}) ] && [ ! $(grep $(date -d "-8 days" +%Y-%m-%d) <<< ${file}) ]
    then
      cp -v ${file} /cygdrive/y/AVA-SiteData/843444/${file}
      # echo ${file} /cygdrive/y/AVA-SiteData/843444/${file}
    fi
  done
fi
net use Y: /delete /y