#!/bin/bash

./gradlew clean \
    pinpitPackageDefaultDistributableZipMacosX64 \
    pinpitPackageDefaultDistributableZipMacosArm64 \
    pinpitPackageDefaultAppImageLinuxX64 \
    pinpitPackageDefaultAppImageLinuxArm64 \
    pinpitPackageDefaultMsiX64
