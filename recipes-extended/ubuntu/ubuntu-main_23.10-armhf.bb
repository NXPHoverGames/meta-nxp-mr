SUMMARY = "A prebuilt main image as baseline for custom work"
require ubuntu-base-image.inc
SECTION = "devel"

APTGET_EXTRA_PACKAGES_MAIN = "1"

# Desktop 23.10 baseline
SRC_URI[md5sum] = "c52f2f8ab15da37803c920916625497a"
SRC_URI[sha256sum] = "c78d8a7fa6668fbb9ae69bdb158813944cabe8c7ba099937177597063d54adb8"

COMPATIBLE_MACHINE = "(ls1021atwr)"
