#!/bin/bash

set -ex

pinpit create-image-assets-from-material-icon --input symbol.svg \
    --output . --color-foreground 0x00BFFF --color-background 0x0A0F1C \
    --color-dialog 0xFFD700 --size-symbol 0.6

mkdir -p ../src/main/resources/
mv icon-192.png ../src/main/resources/forceterm.png

mkdir -p ../src/main/packaging/linux/
mv icon-500.png ../src/main/packaging/linux/forceterm.png

mkdir -p ../src/main/packaging/windows/
mv banner.bmp ../src/main/packaging/windows/
mv dialog.bmp ../src/main/packaging/windows/
mv icon.ico ../src/main/packaging/windows/forceterm.ico

mkdir -p ../src/main/packaging/macos/
mv icon.icns ../src/main/packaging/macos/forceterm.icns
