# Get submodules

	git submodule init
	git submodule update

# Download Okapi Applications

	./download_libs.sh

# Compile HunspellJNA

	cd HunspellJNA
	mvn -Dmaven.test.skip=true -Dmaven.javadoc.skip=true install
	cd ..
	cp $HOME/.m2/repository/dk/dren/hunspell/1.6.2-SNAPSHOT/hunspell-1.6.2-SNAPSHOT.jar  lib/dev/
	cp $HOME/.m2/repository/dk/dren/hunspell/1.6.2-SNAPSHOT/hunspell-1.6.2-SNAPSHOT-sources.jar  lib/dev/

# Create application directory

	./create_dist.sh

# Configure Netbeans

Go to *Project properties > Run*. Set *Working Directory* to **dist**

