#!/bin/bash
set -e
set -o xtrace

ant jar

mkdir -p dist/dictionaries
mkdir -p dist/lib

cp -r dictionaries/*  dist/dictionaries/
cp HunspellJNA/lib/jna.jar  dist/lib/
cp HunspellJNA/native-lib/libhunspell-linux-x86-64.so  dist/lib/
cp languages.txt  dist/
