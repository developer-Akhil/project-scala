#!/bin/bash


if [ -d env_logsys/  ]; then
	rm -rf env_logsys/
  # Control will enter here if $DIRECTORY exists.
fi

python3 -m venv env_logsys
source env_logsys/bin/activate
pip install -r requirements.txt

while true
   do
	python NifiLoggingSystem/com/rxcorp/daqe/etl/NifiLoggingSystem.py
	sleep 3
   done

deativate

exit

#while true
#   do
   	#sudo -u sparky /data/dcpdev/nifi/logsystem/runNifiLog.sh
#	source env_logsys/bin/activate
#	/usr/bin/python /data/dcpdev/nifi/logsystem/NifiLoggingSystem/com/rxcorp/daqe/etl/NifiLoggingSystem.py
#	deactivate
#        sleep 5
#   done


#source /data/dcpdev/nifi/logsystem/env_logsys/bin/activate
#python /data/dcpdev/nifi/logsystem/NifiLoggingSystem/com/rxcorp/daqe/etl/NifiLoggingSystem.py
#deactivate 
