#!/bin/bash
set -e
set -o xtrace

ant jar

MODULE_PATH="OpenXLIFF/lib:dist"
MODULES=Tnt
APPDIR=tnt.AppDir

rm -rf $APPDIR
mkdir $APPDIR
mkdir $APPDIR/dictionaries/
mkdir $APPDIR/lib/
cp deploy/AppDir/* $APPDIR
cp -r OpenXLIFF/catalog/ $APPDIR
cp -r OpenXLIFF/xmlfilter/ $APPDIR
cp -r OpenXLIFF/srx/ $APPDIR
cp -r dictionaries/*  $APPDIR/dictionaries/
cp OpenXLIFF/lib/dtd.jar $APPDIR/lib/
cp OpenXLIFF/lib/json.jar $APPDIR/lib/
cp OpenXLIFF/lib/jsoup-1.11.3.jar $APPDIR/lib/
cp OpenXLIFF/lib/xlifffilters.jar $APPDIR/lib/
cp dist/tntsrc.jar $APPDIR/lib/
cp $HOME/.m2/repository/dk/dren/hunspell/1.6.2-SNAPSHOT/hunspell-1.6.2-SNAPSHOT.jar $APPDIR/lib/
cp HunspellJNA/lib/jna.jar  $APPDIR/lib/
cp HunspellJNA/native-lib/libhunspell-linux-x86-64.so  $APPDIR/lib/
cp languages.txt  $APPDIR/

jlink --add-modules java.base,java.datatransfer,java.desktop,java.logging,java.prefs,java.xml,java.sql --output tnt.AppDir/usr
