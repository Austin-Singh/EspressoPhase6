#!/bin/bash

clear
ant > /dev/null
./espressoc Vince.java > our.txt
./espressocr Vince.java > ref.txt
diff Vince.j Vince.rj > diff.txt
if [ -s diff.txt ]; then
        cat diff.txt
        printf "$(tput setaf 1)NOT MATCHING$(tput sgr0)\n"
else 
        cat our.txt
        printf "$(tput setaf 2)MATCHING$(tput sgr0)\n"
fi
rm our.txt
rm ref.txt
rm diff.txt
ant clean > /dev/null