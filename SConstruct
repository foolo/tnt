import os
import re
import shutil
import subprocess
from os.path import join
env = Environment(ENV = {'PATH' : os.environ['PATH']})
AddOption('--release_version', default="snapshot")
version = GetOption('release_version')
TARGET_LINUX='linux'
TARGET_WINDOWS='windows'
AddOption('--target', default=TARGET_LINUX)
target = GetOption('target')

subprocess.call(["git", "submodule", "init"])
subprocess.call(["git", "submodule", "update"])

appdir = target + ".AppDir"
appdirlib = join(appdir, "lib")
jredir = appdir + "/jre"
release_name = "tnt-" + version
release_file = release_name + {TARGET_LINUX: ".AppImage", TARGET_WINDOWS: ".zip"}[target]

try:
	shutil.rmtree(appdir)
except FileNotFoundError:
	pass

env.Command("dist/tnt.jar", None, "ant jar")
env.AlwaysBuild("dist/tnt.jar")
jre_artifact = "tnt-artifacts/" + {TARGET_LINUX: "tnt-jre-linux-x64.7z", TARGET_WINDOWS: "tnt-jre-windows-x64.7z"}[target]
env.Command(jredir, None, "7z x " + jre_artifact + " -o" + appdir)

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

if target == TARGET_WINDOWS:
	SConscript(['deploy/windows_src/SConscript'])
	env.Install(appdir, "deploy/windows_src/tnt.exe")
if target == TARGET_LINUX:
	add_resources(appdir, "deploy/linux")

add_resources(join(appdir, "catalog"),   "OpenXLIFF/catalog")
add_resources(join(appdir, "xmlfilter"), "OpenXLIFF/xmlfilter")
add_resources(join(appdir, "srx"),       "OpenXLIFF/srx")
add_resources(join(appdir, "dictionaries"), "dictionaries", "(.dic|.aff)$")
env.Install(appdirlib, "OpenXLIFF/lib/dtd.jar")
env.Install(appdirlib, "OpenXLIFF/lib/json.jar")
env.Install(appdirlib, "OpenXLIFF/lib/jsoup-1.11.3.jar")
env.Install(appdirlib, "OpenXLIFF/lib/openxliff.jar")
env.Install(appdirlib, "dist/tnt.jar")
env.Install(appdirlib, "tnt-artifacts/hunspell-1.6.2-SNAPSHOT.jar")
env.Install(appdirlib, "HunspellJNA/lib/jna.jar")

native_libs = {TARGET_LINUX: "HunspellJNA/native-lib/libhunspell-linux-x86-64.so", TARGET_WINDOWS: "HunspellJNA/native-lib/hunspell-win-x86-64.dll"}
env.Install(appdirlib, native_libs[target])

def zipdir(target, source, env):
	if (len(target) != 1) or (len(source) != 1):
		exit_error("unexpected target/source: " + str(target) + ", " + str(source))
	zip_name = re.sub("\.zip$", "", str(target[0]))
	shutil.make_archive(zip_name, 'zip', str(source[0]))

if target == TARGET_WINDOWS:
	env.Command(release_file, appdir, zipdir)
if target == TARGET_LINUX:
	env.Command(release_file, appdir, "appimagetool " + appdir + " " + release_file)

env.AlwaysBuild(release_file)
