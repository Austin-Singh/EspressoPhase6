#!/bin/bash

RED=$(tput setaf 1)
GREEN=$(tput setaf 2)
NORMAL=$(tput sgr0)

function runTest {

        ./espressoc $1 > our.txt
        ./espressocr $1 > ref.txt

        sed '/^;/d' *.rj > refj.txt
        sed '/^;/d' *.j > ourj.txt
        diff refj.txt ourj.txt > diff.txt

        if [ -s diff.txt ]; then
                ((badResults=badResults+1))
                printf "%-15s %-110s %-15s\n" "Processing ..." $f "${RED}NOT MATCHING [$total]${NORMAL}"

                #cat diff.txt
                printf "\n\n"
        else 
                ((goodResults=goodResults+1))
                #printf "%-15s %-110s %-15s\n" "Processing ..." $f "${GREEN}MATCHING [$total]${NORMAL}"

                #cat our.txt
                ##printf "\n\n"
        fi

        find . -name "*.j" -type f -delete
        find . -name "*.rj" -type f -delete

}

clear
ant > /dev/null

tests="/home/wsl/EspressoPhase6/Tests/Phase6/Espresso/GoodTests/*"
goodResults=0
badResults=0
total=0
for f in $tests
do
        ((total=total+1))
        runTest $f
done

ant clean > /dev/null
printf "Passed Tests: ${GREEN}$goodResults${NORMAL} of $total passed (${RED}$badResults${NORMAL} failed)\n\n"
rm diff.txt
rm ref.txt
rm our.txt
rm ourj.txt
rm refj.txt


