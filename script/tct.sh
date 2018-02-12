#!/bin/sh

cat << "EOF"
  ____  _              _          _____
 / ___|| |_ _ __ _   _| | _____  |  ___|__  _ __ ___ ___
 \___ \| __| '__| | | | |/ / _ \ | |_ / _ \| '__/ __/ _ \
  ___) | |_| |  | |_| |   <  __/ |  _| (_) | | | (_|  __/
 |____/ \__|_|   \__, |_|\_\___| |_|  \___/|_|  \___\___|
                 |___/

EOF
echo stopping robot...
/etc/init.d/nilvrt stop
/usr/local/frc/bin/frcKillRobot.sh
echo tct starting, please wait...
echo
/usr/local/frc/JRE/bin/java -Djava.library.path=/usr/local/frc/lib/ -jar tct.jar 2> /dev/null
echo
echo reboot roboRIO to restart robot code.
