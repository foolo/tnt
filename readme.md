# get submodules

	git submodule init
	git submodule update


# build and deploy for windows

download and extract:
https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_windows-x64_bin.zip
http://apache.mirrors.spacedump.net//ant/binaries/apache-ant-1.10.6-bin.zip

from bash prompt (e.g. cygwin or git bash) run

	PATH="/path/to/jdk-12.0.1/bin:/path/to/apache-ant-1.10.6/bin:$PATH"
	deploy/deploy_windows.sh
