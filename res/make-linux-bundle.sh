#!/bin/bash
if [ "$DEBUG" != "" ];
then
    export DEBUG="-DDEBUG"
fi
v="`ls ddswriter-bundle-*.jar`"
v=${v%.*}
v=${v##*-}
URL="https://bitbucket.org/alexkasko/openjdk-unofficial-builds/downloads/openjdk-1.7.0-u80-unofficial-linux-amd64-image.zip"
CACHED_FILE="../../tmp/`echo \"$URL\" |  sha256sum | cut -d ' ' -f1`.zip"
if [ ! -f "$CACHED_FILE" ];
then
    wget "$URL" -O "$CACHED_FILE"
    hash="`sha256sum $CACHED_FILE | cut -d ' ' -f 1`" 
    if [ "$hash" != "b87beb73f07af5b89b35b8656439c70fb7f46afdaa36e4a9394ad854c3a0b23d" ];
    then
        echo "Corrupted JRE"
        exit 1
    fi
else 
    echo "File cached $CACHED_FILE"
fi

unzip "$CACHED_FILE"
mv openjdk-*-linux-*-image linux-bundle
rm -Rf linux-bundle/src.zip
rm -Rf linux-bundle/man
cp ddswriter-bundle-*.jar linux-bundle/ddswriter.jar
tar -czvf linux-bundle.tar.gz -C linux-bundle/ .
# cat LinuxLauncher.sh linux-bundle.tar.gz  > DDSWriter-$v-linux32 
# mkdir -p dist
# mv DDSWriter-$v-linux32 dist/
# rm lin.zip
# rm -Rvf linux-bundle
# rm linux-bundle.tar.gz


rm -Rf build-llauncher
mkdir  build-llauncher
mkdir -p dist

xxd -i LinuxLauncher.sh > build-llauncher/LinuxLauncher.h
cp linux-bundle.tar.gz  build-llauncher/linux-bundle.tar.gz
cp Launcher.c build-llauncher/Launcher.c
cd build-llauncher/
shared_root="DDSw-build-`date +%Y%m%d_%H%M%S`"
gcc Launcher.c -o Launcher.bin -DSHARED_ROOT=$shared_root -DLINUX $DEBUG
echo -e "\n__ARCHIVE_BELOW__" > sep.tmp
cat Launcher.bin sep.tmp linux-bundle.tar.gz  >  ../dist/DDSWriter-$v-linux64
chmod +x  ../dist/DDSWriter-$v-linux64
rm sep.tmp
cd ..
# mv DDSWriter-$v-linux32 dist/

# rm lin.zip
# rm -Rvf windows-bundle
# rm windows-bundle.7z