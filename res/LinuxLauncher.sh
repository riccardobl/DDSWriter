#!/bin/sh

ROOT="`dirname $0`"
if [ ! -f "$ROOT/READY" ];
then
    export ARCHIVE=`awk '/^__ARCHIVE_BELOW__/ {print NR + 1; exit 0; }'  "$ROOT/linux-bundle.tar.gz"`
    tail -n+$ARCHIVE "$ROOT/linux-bundle.tar.gz" | tar --skip-old-files -xz -C $ROOT > /dev/null
    echo "1">"$ROOT/READY"
fi
VM_ARGS="-XX:+AggressiveOpts  -XX:+UseG1GC -XX:-UseGCOverheadLimit"
ARGS=$@
if [ "$1" = "--vmArgs" ];
then
    VM_ARGS=$VM_ARGS $2
    ARGS=${@:3}
fi
"$ROOT/jre/bin/java" $VM_ARGS   -DnativePath="$ROOT" -jar "$ROOT/ddswriter.jar" $ARGS
