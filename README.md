# Get submodules

	git submodule init
	git submodule update

# Prerequisities

## Linux

Install JDK 11, Apache Ant and Apache Maven

Add all ```/bin``` directories to PATH

Install scons (e.g. ```sudo apt install scons``` on Ubuntu )

## Windows

Download and extract

https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_windows-x64_bin.zip

http://apache.mirrors.spacedump.net//ant/binaries/apache-ant-1.10.6-bin.zip

http://apache.mirrors.spacedump.net/maven/maven-3/3.6.1/binaries/apache-maven-3.6.1-bin.zip

Download and install Python 3 from https://www.python.org/downloads/
Make sure to check "add Python to PATH" and similar.

Install scons

	pip install scons

Before running the build commands under Windows, set the PATH (assuming tools were extracted under %userprofile%)

	set PATH="%userprofile%\apache-ant-1.10.6\bin;%userprofile%\apache-maven-3.6.1\bin;%userprofile%\jdk-12.0.1\bin";%PATH%

# Create release directory

	scons

The application can now be run with ```tnt.AppDir/AppRun``` on Linux or ```tnt.AppDir\runtnt.bat``` on Windows.

## Create Linux AppImage

To create an AppImage, download appimagetool from https://github.com/AppImage/AppImageKit/releases and run

	./appimagetool-x86_64.AppImage tnt.AppDir

# Configure Netbeans

Open the project in Netbeans 11 or later. Under *Project properties > Run*, set *Working Directory* to the tnt.AppDir directory created in previous step.
