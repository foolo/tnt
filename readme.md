# Get submodules

	git submodule init
	git submodule update

# Prerequisities

## Linux

Install JDK 11 and Apache Ant. Add ```/bin``` directories to PATH.

Download appimagetool from https://github.com/AppImage/AppImageKit/releases and make it executable as ```appimagetool``` from PATH.

Install scons (e.g. ```sudo apt install scons``` on Ubuntu). Scons must be run with Python 3.

# Create release

	scons --target=TARGET --release_version=VERSION

# Configure Netbeans

Open the project in Netbeans 11 or later. Under *Project properties > Run*, set *Working Directory* to the tnt.AppDir directory created in previous step.

# Creating artifacts

## JRE artifacts

On corresponding platform, run:

	jlink --add-modules java.base,java.datatransfer,java.desktop,java.logging,java.prefs,java.xml,java.sql --output jre
	7z a tnt-jre-PLATFORM.7z jre

## HunspellJNA artifacts

To build ```hunspell-1.6.2-SNAPSHOT.jar``` and ```hunspell-1.6.2-SNAPSHOT-sources.jar``` in ```HunspellJNA/build/jar```, run:

	cd HunspellJNA
	mvn -Dmaven.test.skip=true -Dmaven.javadoc.skip=true package
