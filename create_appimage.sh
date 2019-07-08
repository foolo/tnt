#!/bin/bash
set -e

MODULES=Tnt
APPDIR=tnt.AppDir

mkdir $APPDIR
cp AppDir/* $APPDIR
cp -r OpenXLIFF/catalog/ $APPDIR
cp -r OpenXLIFF/xmlfilter/ $APPDIR
cp -r OpenXLIFF/srx/ $APPDIR

cp dist/tntsrc.jar OpenXLIFF/lib/
rm -rf tnt.AppDir/usr
jlink --module-path OpenXLIFF/lib --add-modules $MODULES --output tnt.AppDir/usr

# create .AppImage with
# appimagetool-x86_64.AppImage tnt.AppDir
