#!/bin/bash

ant > /dev/null
./espressoc Vince.java > our.txt
./espressocr Vince.java > ref.txt
diff Vince.j Vince.rj > diff.txt
if [ -s diff.txt ]; then
        printf "$(tput setaf 1)NOT MATCHING$(tput sgr0)\n"
        cat diff.txt
else 
        printf "$(tput setaf 2)MATCHING$(tput sgr0)\n"
fi
rm our.txt
rm ref.txt
rm diff.txt
ant clean > /dev/null