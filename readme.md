# Get submodules

	git submodule init
	git submodule update

# Build and run

Option 1: Open the project in Netbeans 11 or later. Configure the project's working directory to the OpenXLIFF subdirectory.

Option 2: Build and run from command line. See details below.

# Build and deploy for Linux

Make sure that **java** and **ant** binary directories are in PATH and run:

	deploy/create_appdir.sh

The application can now be run with

	tnt.AppDir/AppRun

To create an AppImage, download appimagetool from https://github.com/AppImage/AppImageKit/releases and run

	./appimagetool-x86_64.AppImage tnt.AppDir


# Build and deploy for Windows

Download and extract
https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_windows-x64_bin.zip and
http://apache.mirrors.spacedump.net//ant/binaries/apache-ant-1.10.6-bin.zip

From Bash prompt (e.g. Cygwin or Git Bash) run

	PATH="/path/to/jdk-12.0.1/bin:/path/to/apache-ant-1.10.6/bin:$PATH"
	deploy/deploy_windows.sh

The application can now be run with ```tnt.winapp/runtnt.bat```
