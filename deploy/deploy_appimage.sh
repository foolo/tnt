#!/bin/bash
set -e
set -o xtrace

ant jar

MODULE_PATH="OpenXLIFF/lib:dist"
MODULES=Tnt
APPDIR=tnt.AppDir

rm -rf $APPDIR
mkdir $APPDIR
cp deploy/AppDir/* $APPDIR
cp -r OpenXLIFF/catalog/ $APPDIR
cp -r OpenXLIFF/xmlfilter/ $APPDIR
cp -r OpenXLIFF/srx/ $APPDIR

jlink --module-path $MODULE_PATH --add-modules $MODULES --output $APPDIR/usr
