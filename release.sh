#!/bin/bash

set -e

./gradlew clean \
    pinpitPackageDefaultDistributableZipMacosX64 \
    pinpitPackageDefaultDistributableZipMacosArm64 \
    pinpitPackageDefaultAppImageLinuxX64 \
    pinpitPackageDefaultAppImageLinuxArm64 \
    pinpitPackageDefaultMsiX64

DIR=dist

rm -rf $DIR
mkdir $DIR

FILES=$(find build -type f -and \( -name "*AppImage" -or -name "*.zip" -or -name "*msi" \))

for f in $FILES; do
    echo $f;
    cp $f $DIR;
done;
