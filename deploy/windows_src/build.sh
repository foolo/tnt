#!/bin/bash
set -o xtrace
set -e
magick convert ../../src/images/Gnome-accessories-character-map_48.png icon.ico
windres tnt.rc --output-format=coff --output=tnt.res
g++ tnt_exe.cpp tnt.res -mwindows -o ../windows/tnt.exe
