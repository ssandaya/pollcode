(echo -e \\001I@9H00; sleep 3; echo -e \\035quit; sleep 1) | telnet 10.20.95.93 10001

(echo -e \\001I@9H00; sleep 3; echo -e \\035quit; sleep 1) | telnet 10.20.95.93 10001 > /home/simulate/temp/Iat9H00_$(date +%Y%m%d_%H%M%S).txt
