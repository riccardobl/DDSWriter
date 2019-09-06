#!/bin/bash
set -e
mkdir -p tmp

./gradlew clean
if [ "$1" = "travis" ];
then
    WINDOWS=1
    LINUX=1
    if [ "$TRAVIS_TAG" != "" ];
    then
        args="-Pin_version=$TRAVIS_TAG"
    else
        args="-Pin_version=$TRAVIS_COMMIT"
    fi
else 
    args=$@
fi


./gradlew build buildBundle $args
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