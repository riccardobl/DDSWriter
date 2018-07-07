#!/bin/bash
v="`ls ddswriter-bundle-*.jar`"
v=${v%.*}
v=${v##*-}
mkdir -p dist
cp ddswriter-bundle-*.jar   dist/DDSWriter-$v.jar
