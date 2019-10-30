# Get submodules

	git submodule init
	git submodule update

# Prerequisities

## Linux

Install JDK 11 and Apache Maven. Add ```/bin``` directories to PATH.

Download appimagetool from https://github.com/AppImage/AppImageKit/releases and make it executable as ```appimagetool``` from PATH.

Install scons (e.g. ```sudo apt install scons``` on Ubuntu). Scons must be run with Python 3.

Add artifacts to local Maven repository:

	mvn install:install-file -Dfile=OpenXLIFF/lib/dtd.jar -DgroupId=local.com.wutka -DartifactId=dtdparser -Dversion=1.16 -Dpackaging=jar -DgeneratePom=true
	mvn install:install-file -Dfile=OpenXLIFF/lib/json.jar -DgroupId=local.org.json -DartifactId=json -Dversion=0.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
	mvn install:install-file -Dfile=OpenXLIFF/lib/jsoup-1.11.3.jar -DgroupId=local.org.jsoup -DartifactId=jsoup -Dversion=1.11.3 -Dpackaging=jar -DgeneratePom=true
	mvn install:install-file -Dfile=OpenXLIFF/lib/mapdb.jar -DgroupId=local.org.mapdb -DartifactId=mapdb -Dversion=0.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
	mvn install:install-file -Dfile=OpenXLIFF/lib/openxliff.jar -DgroupId=com.maxprograms -DartifactId=openxliff -Dversion=1.5.1 -Dpackaging=jar -DgeneratePom=true
	mvn install:install-file -Dfile=HunspellJNA/lib/jna.jar -DgroupId=local.net.java.dev.jna -DartifactId=jna -Dversion=3.4.0 -Dpackaging=jar -DgeneratePom=true
	mvn install:install-file -Dfile=tnt-artifacts/hunspell-1.6.2-SNAPSHOT.jar -DgroupId=local.dk.dren -DartifactId=hunspell -Dversion=1.6.2-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

# Create release

	scons --target=TARGET --release_version=VERSION

# Configure Netbeans

Open the project in Netbeans 11 or later. Under *Project properties > Run*, set *Working Directory* to the tnt.AppDir directory created in previous step.

# Creating artifacts and resources

## JRE artifacts

On corresponding platform, run:

	jlink --add-modules java.base,java.datatransfer,java.desktop,java.logging,java.prefs,java.xml,java.sql --output jre
	7z a tnt-jre-PLATFORM.7z jre

## HunspellJNA artifacts

To build ```hunspell-1.6.2-SNAPSHOT.jar``` and ```hunspell-1.6.2-SNAPSHOT-sources.jar``` in ```HunspellJNA/build/jar```, run:

	cd HunspellJNA
	mvn -Dmaven.test.skip=true -Dmaven.javadoc.skip=true package

## Windows icon

Create icon.ico with ImageMagick convert tool:

	convert src/images/Gnome-accessories-character-map_48.png deploy/windows_src/icon.ico

## tnt.exe

Install mingw-w64 (```sudo apt install g++-mingw-w64-x86-64``` on Ubuntu)

Build tnt.exe:

	cd deploy/windows_src
	scons --file SConscript
