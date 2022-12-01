#!/bin/bash

clear
ant > /dev/null
./espressoc Vince.java > our.txt
./espressocr Vince.java > ref.txt

sed '/^;/d' Vince.rj > refj.txt
sed '/^;/d' Vince.j > ourj.txt
diff refj.txt ourj.txt > diff.txt

if [ -s diff.txt ]; then
        cat our.txt
        cat diff.txt
        printf "$(tput setaf 1)NOT MATCHING$(tput sgr0)\n"
else 
        cat our.txt
        printf "$(tput setaf 2)MATCHING$(tput sgr0)\n"
fi
rm our.txt
rm ref.txt
rm refj.txt
rm ourj.txt
rm Vince.rj
rm Vince.j
rm diff.txt
ant clean > /dev/null