#!/bin/bash
set -e
if [ "$DEBUG" != "" ];
then
    export DEBUG="-DDEBUG"
fi
v="`ls ddswriter-bundle-*.jar`"
v=${v%.*}
v=${v##*-}
URL="https://cdn.azul.com/zulu/bin/zulu8.50.0.51-ca-jre8.0.275-linux_x64.zip"
CACHED_FILE="../../tmp/`echo \"$URL\" |  sha256sum | cut -d ' ' -f1`.zip"
if [ ! -f "$CACHED_FILE" ];
then
    wget "$URL" -O "$CACHED_FILE"
    hash="`sha256sum $CACHED_FILE | cut -d ' ' -f 1`" 
    if [ "$hash" != "5603a48f67c6791d132a825815f49fa432709a1d352f114d025ef25452ffe7fa" ];
    then
        echo "Corrupted JRE"
        exit 1
    fi
else 
    echo "File cached $CACHED_FILE"
fi

unzip "$CACHED_FILE"
mv zulu* linux-bundle
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