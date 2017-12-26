(echo -e \\001I@9H00; sleep 3; echo -e \\035quit; sleep 1) | telnet 10.20.95.93 10001 
sleep 10
(echo -e \\001I@9H00; sleep 3; echo -e \\035quit; sleep 1) | telnet 10.20.95.93 10001 > /home/simulate/temp/Iat9H00_450p_$(date +%Y%m%d_%H%M%S).txt 
