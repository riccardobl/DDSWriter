#!/bin/bash
./gradlew clean

if [ "$1" = "travis" ];
then
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
    ./make-linux-bundle.sh
fi
if [ "$WINDOWS" != "" ];
then
    ./make-windows-bundle.sh
fi
./make-generic-bundle.sh
