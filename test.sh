#!/bin/bash
./espressoc Vince.java > our.txt
./espressocr Vince.java > ref.txt
diff Vince.j Vince.rj > diff.txt
cat diff.txt