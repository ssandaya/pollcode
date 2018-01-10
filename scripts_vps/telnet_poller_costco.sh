#!/bin/bash -x
source ./config/$1
rs232_command="$2"
file_desc="$3"

echo "rs232_command: ${rs232_command}"
echo "file_desc: ${file_desc}"
sleep 3


#!/bin/bash 

SOURCE_DIR="/cygdrive/c/Users/simulate/VPS-PollData"
if [ ! -d "${SOURCE_DIR}" ] ; then
  # net use N: \\\\gvrsimvap01\\Share\\N /user:sandaya BdSa998466
  mkdir -p ${SOURCE_DIR}
  if [ ! -d "${SOURCE_DIR}" ] ; then
    echo "Destination dir ${SOURCE_DIR} not found!"
    exit 1
  fi
fi


date_today="$(date +'%Y%m%d')"
date_time="$(date +'%Y%m%d%H%M%S')"
#i@5501
dest_dir="${SOURCE_DIR}/Site${site_id}/${rs232_command}"
file_path="${dest_dir}/${site_id}_${file_desc}_${date_time}-0000.txt"
if [ ! -d "${dest_dir}" ]; then
  mkdir -p ${dest_dir}
fi
sleep 5
(echo -e \\001${rs232_command}; sleep 5; echo -e \\035quit; sleep 5;) | telnet ${console_ip} ${console_port} | grep -i "${rs232_command}" >  ${file_path}
wait
sync -f ${file_path}
cat ${file_path}
sleep 3
exit 0
