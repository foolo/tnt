import os
import re
import platform
from os.path import join
env = Environment(ENV = {'PATH' : os.environ['PATH']})

TARGET_LINUX='linux'
TARGET_WINDOWS='windows'
target = platform.system().lower()
appdir = "tnt.AppDir"
appdirlib = join(appdir, "lib")
jredir = appdir + "/jre"

env.Command("HunspellJNA/build/jar/hunspell-1.6.2-SNAPSHOT.jar", None, "mvn -f HunspellJNA/pom.xml -Dmaven.test.skip=true -Dmaven.javadoc.skip=true package")
env.AlwaysBuild("HunspellJNA/build/jar/hunspell-1.6.2-SNAPSHOT.jar")
env.Command("dist/tntsrc.jar", None, "ant jar")
env.AlwaysBuild("dist/tntsrc.jar")

modules = "java.base,java.datatransfer,java.desktop,java.logging,java.prefs,java.xml,java.sql"
jlink_cmd = "jlink --add-modules " + modules + " --output " + jredir
env.Command(jredir, None, jlink_cmd)

def exit_error(ex):
	raise Exception(ex)
	exit(1)

def add_resources(target_path, res_path, pattern=None):
	for path, dirs, files in os.walk(res_path, onerror=exit_error):
		if pattern:
			files = [x for x in files if re.search(pattern, x)]
		for f in files:
			relp = os.path.relpath(path, res_path)
			env.Install(join(target_path, relp), join(path, f))

add_resources(appdir, join("deploy", target))
add_resources(join(appdir, "catalog"),   "OpenXLIFF/catalog")
add_resources(join(appdir, "xmlfilter"), "OpenXLIFF/xmlfilter")
add_resources(join(appdir, "srx"),       "OpenXLIFF/srx")
add_resources(join(appdir, "dictionaries"), "dictionaries", "(.dic|.aff)$")
env.Install(appdirlib, "OpenXLIFF/lib/dtd.jar")
env.Install(appdirlib, "OpenXLIFF/lib/json.jar")
env.Install(appdirlib, "OpenXLIFF/lib/jsoup-1.11.3.jar")
env.Install(appdirlib, "OpenXLIFF/lib/xlifffilters.jar")
env.Install(appdirlib, "dist/tntsrc.jar")
env.Install(appdirlib, "HunspellJNA/build/jar/hunspell-1.6.2-SNAPSHOT.jar")
env.Install(appdirlib, "HunspellJNA/lib/jna.jar")
env.Install(appdir, "languages.txt")

native_libs = {TARGET_LINUX: "HunspellJNA/native-lib/libhunspell-linux-x86-64.so", TARGET_WINDOWS: "HunspellJNA/native-lib/hunspell-win-x86-64.dll"}
env.Install(appdirlib, native_libs[target])

