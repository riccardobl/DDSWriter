#!/bin/bash
set -e
# Win dependencies:
#       choco install wget 7z 
#       wget http://www.labtestproject.com/files/sha256sum/sha256sum.exe -O /bin/sha256sum.exe
#       (mingw)
if [ "$DEBUG" != "" ];
then
    export DEBUG="-DDEBUG"
fi
v="`ls ddswriter-bundle-*.jar`"
v=${v%.*}
v=${v##*-}

URL="https://cdn.azul.com/zulu/bin/zulu8.50.0.51-ca-jre8.0.275-win_x64.zip"
CACHED_FILE="../../tmp/`echo \"$URL\" |  sha256sum | cut -d ' ' -f1`.zip"
if [ ! -f "$CACHED_FILE" ];
then
    wget "$URL" -O "$CACHED_FILE"
    hash="`sha256sum $CACHED_FILE | cut -d ' ' -f 1`" 
    if [ "$hash" != "65e08595f0b542712d84873da6f48ac1ec1d13aeadd50e86684fb1d9de691d84" ];
    then
        echo "Corrupted JRE"
        exit 1
    fi
else
    echo "File cached $CACHED_FILE"
fi


unzip "$CACHED_FILE"
mkdir windows-bundle
mv zulu* windows-bundle/jre
rm -Rf windows-bundle/jre/src.zip
cp ddswriter-bundle-*.jar windows-bundle/ddswriter.jar

wget "https://www.7-zip.org/a/7z1805.exe" -O 7z.exe
hash="`sha256sum 7z.exe | cut -d ' ' -f 1`" 
if [ "$hash" != "647a9a621162cd7a5008934a08e23ff7c1135d6f1261689fd954aa17d50f9729" ];
then
    echo "Corrupted 7z"
    exit 1
fi
mkdir -p 7z
7z x 7z.exe -o7z
rm 7z.exe
cp 7z/7z.exe 7z.exe
cp 7z/7z.dll 7z.dll
cd windows-bundle
7z a -t7z ../windows-bundle.7z *
cd ..

rm -Rf build-wlauncher
mkdir  build-wlauncher
mkdir -p dist
xxd -i 7z.exe > build-wlauncher/7z.h
xxd -i 7z.dll > build-wlauncher/7z_dll.h
xxd -i WindowsLauncher.bat > build-wlauncher/WindowsLauncher.h
cp windows-bundle.7z  build-wlauncher/windows-bundle.7z
cp Launcher.c build-wlauncher/Launcher.c
cd build-wlauncher/
shared_root="DDSw-build-`date +%Y%m%d_%H%M%S`"

if [[ "`uname -a`" == MINGW* ]];
then
    GCC="gcc"
else
    GCC="i686-w64-mingw32-gcc"
fi

"$GCC" Launcher.c -o WindowsLauncher.exe -DSHARED_ROOT=$shared_root -DWINDOWS $DEBUG
cat WindowsLauncher.exe windows-bundle.7z >  ../dist/DDSWriter-$v-win64.exe
cd ..
# mv DDSWriter-$v-win32.exe dist/
# rm -Rvf 7z
# rm 7z.exe
# rm 7z.dll
# rm win.z/ip
# rm -Rvf windows-bundle
# rm windows-bundle.7z
