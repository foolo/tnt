#!/bin/bash
set -o xtrace
set -e
windres tnt.rc --output-format=coff --output=tnt.res
g++ tnt_exe.cpp tnt.res -mwindows -o ../windows/tnt.exe
