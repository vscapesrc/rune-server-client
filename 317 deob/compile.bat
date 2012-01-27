@echo off
title Compiling...
cd src
"C:\Program Files\Java\jdk1.6.0_25\bin\javac.exe" -cp . -d ../bin/ ./*java
pause