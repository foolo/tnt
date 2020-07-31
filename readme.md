# Introduction

TNT is a computer-aided translation tool written in Java.

For information about downloading and using the application, visit the [homepage](https://foolo.github.io/tnt-homepage/).

# Building the application

## Clone the repository and download submodules

	git clone git@github.com:foolo/tnt.git
	git submodule init
	git submodule update

## Install prerequisites

Install JDK 11 or later and Apache Ant. Add ```/bin``` directories to PATH.

Download appimagetool from https://github.com/AppImage/AppImageKit/releases and make it executable as ```appimagetool``` from PATH.

Install scons (e.g. ```sudo apt install scons``` on Ubuntu). Scons must be run with Python 3.

## Create release

Update APPLICATION_VERSION in Application.java

Build using the same version

	scons --target=TARGET --release_version=APPLICATION_VERSION

A directory called ```<TARGET>.AppDir``` will be created, as well as a release file (an .AppImage file for Linux or a .zip file for Windows)

## Configuring Netbeans

Open the project in Netbeans 11 or later. Under *Project properties > Run*, set *Working Directory* to the tnt.AppDir directory created in previous step.

## Creating artifacts and resources

### JRE artifacts

On corresponding platform, run:

	jlink --add-modules java.base,java.datatransfer,java.desktop,java.logging,java.prefs,java.xml,java.sql --output jre
	7z a tnt-jre-PLATFORM.7z jre

### HunspellJNA artifacts

To build ```hunspell-1.6.2-SNAPSHOT.jar``` and ```hunspell-1.6.2-SNAPSHOT-sources.jar``` in ```HunspellJNA/build/jar```, run:

	cd HunspellJNA
	mvn -Dmaven.test.skip=true -Dmaven.javadoc.skip=true package

### Windows icon

Create icon.ico with ImageMagick convert tool:

	convert src/images/Gnome-accessories-character-map_48.png deploy/windows_src/icon.ico

### tnt.exe

Install mingw-w64 (```sudo apt install g++-mingw-w64-x86-64``` on Ubuntu)

Build tnt.exe:

	cd deploy/windows_src
	scons --file SConscript
