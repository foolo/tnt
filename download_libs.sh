#!/bin/bash
set -e
set -o xtrace
mkdir -p lib/dev
cd lib/dev

OKAPI=okapi-apps_gtk2-linux-x86_64_0.37
wget https://bintray.com/okapi/Distribution/download_file?file_path=$OKAPI.zip -O $OKAPI.zip
mkdir $OKAPI
unzip $OKAPI.zip -d $OKAPI
