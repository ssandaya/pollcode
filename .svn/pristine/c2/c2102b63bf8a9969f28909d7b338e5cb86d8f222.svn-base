#!/bin/bash  -vx
ls -al

str_file="0050831045b2-20170630043300-0000.txt"
DEST_FILE="C:/Users/simulate/dev/projects/polldata2/scripts/fileloopxxxx.sh"
date_past=$(date -d "-10 days" +%Y%m%d)
date_today=$(date +%Y%m%d)
echo "$date_file $date_past $date_today"

SOURCE_DIR=//gvrsimvap01/Share/N/poll/data/
# if [[ ! -f "$SOURCE_DIR" ]] ; then
# 	echo "${SOURCE_DIR} not found!"
# 	exit 1
# fi	
cd $SOURCE_DIR
ls

if [  $? > 0  ]; then
   echo "${SOURCE_DIR} not found!"
   exit 1
fi
ls -ah
net use N: \\\\gvrsimvap01\\Share\\N
/cygdrive/c/Users/simulate/dev/projects/polldata2/exe/gurps.exe N:/poll/data/0050831045b2/i@T401/0050831045b2-20170716134300-0000.txt

date_file=${str_file:13:8}
echo $date_file
filename="${date_file:0:4}-${date_file:4:2}-${date_file:6:2}.csv"

countMt=0
countMt=`expr $countMt + 1`
echo "count1: $countMt $countUp"
countMt=`expr $countMt + 1`
echo "count1: $countMt $countUp"

# date filter and file exist filter
if [ $date_file -gt $date_past ] ; then
  echo "process 0"
  if [ $date_file -ne $date_today ] ; then
    echo "process 1"
    if [ ! -f $DEST_FILE ]; then
      echo "process 2"
    fi
  fi
fi