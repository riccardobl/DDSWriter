#!/bin/bash
set -e
mkdir -p tmp

gradle clean

gradle build buildBundle -Pin_version=$VERSION
cd build/libs
cp -Rvf ../../res/* .
chmod +x *.sh
rm -Rvf dist
if [ "$LINUX" != "" ];
then
    echo "Build linux bundle"
    ./make-linux-bundle.sh
fi
if [ "$WINDOWS" != "" ];
then
    echo "Build windows bundle"
    ./make-windows-bundle.sh
fi

echo "Build generic bundle"
./make-generic-bundle.sh

ls -lh dist/
echo "Done"