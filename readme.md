# Get submodules

	git submodule init
	git submodule update

# Prerequisities

## Linux

Install JDK 11, Apache Ant and Apache Maven

Add all ```/bin``` directories to PATH

Download appimagetool from https://github.com/AppImage/AppImageKit/releases and make it executable as ```appimagetool``` from PATH

Install scons (e.g. ```sudo apt install scons``` on Ubuntu)

Scons must be run with Python 3

# Create release

	scons --target=TARGET --release_version=VERSION

# Configure Netbeans

Open the project in Netbeans 11 or later. Under *Project properties > Run*, set *Working Directory* to the tnt.AppDir directory created in previous step.

# Creating JRE artifact

On corresponding platform, run:

	jlink --add-modules java.base,java.datatransfer,java.desktop,java.logging,java.prefs,java.xml,java.sql --output jre
	7z a tnt-jre-PLATFORM.7z jre
