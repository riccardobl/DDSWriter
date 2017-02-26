#!/bin/bash
shopt -s globstar
for f in **/*.java; do
  #
    if ! grep -q "Copyright" "$f" ;
    then
        echo $f
        authors=`cat "$f" | grep  -oP "\*\\s*@author\s+\K(.+)" | tr '\n' ','  |  sed 's/.$//'`
        if [ "$authors" != "" ];
        then
            license="`cat LICENSE | sed "s/@author/$authors/g"`"
            echo "/**
$license
*/" > /tmp/___licenseGen.tmp
            cat "$f" >> /tmp/___licenseGen.tmp
            mv /tmp/___licenseGen.tmp $f
        

        fi
     fi
done

