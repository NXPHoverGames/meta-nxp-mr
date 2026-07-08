SUMMARY = "NXP LA9310 FreeRTOS dfe_app firmware (la9310_dfe.bin)"
DESCRIPTION = "Builds the LA9310 FreeRTOS dfe_app firmware for the LimeSDR-Micro \
target (PCIe host mode) as described in the NXP LimeSDR-Micro porting guide \
(section 6.4), and installs it as /lib/firmware/la9310_dfe.bin. The firmware \
runs on the LA9310 Cortex-M4 and is built with the arm-none-eabi 6-2017-q2 \
toolchain. Only the FreeRTOS dfe_app is built here (not the e200/VSPA firmwares)."
HOMEPAGE = "https://github.com/nxp-qoriq/la93xx_freertos"

# BSD-3-Clause/GPL-2.0 (NXP LA9310 sources) plus MIT (bundled FreeRTOS kernel).
LICENSE = "BSD-3-Clause & GPL-2.0-only & MIT"
LIC_FILES_CHKSUM = "file://License/bsd-3-clause.txt;md5=0f00d99239d922ffd13cabef83b33444 \
                    file://License/COPYING;md5=19aa8c9d585fed072ae2ba598dceae23 \
                    file://License/license.txt;md5=87aee241eee7def2eb5188b1d967d5c7"

PV = "1.0"

# Bare-metal Cortex-M4 firmware: no target libc/compiler deps. It is built with
# the prebuilt arm-none-eabi 6-2017-q2 toolchain (guide section 6.1.2) via CMake.
INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "gcc-arm-none-eabi-6-2017q2-native cmake-native"

# Three sources (AUTOREV, matching the la93xx-host-sw recipe convention):
#  - la93xx_freertos : the firmware sources (primary -> ${S}).
#  - la93xx_host_sw  : provides uapi/ headers (LA9310_HOST_SW_UAPI).
#  - LimeSuiteNG     : LimeSDR-Micro (lms7002m) sources; the CMakeLists hardcodes
#                      SET(LIMESUITENG ${ProjDirPath}/../../../LimeSuiteNG), which
#                      resolves to ${UNPACKDIR}/LimeSuiteNG.
SRC_URI = " \
    git://github.com/nxp-qoriq/la93xx_freertos.git;protocol=https;branch=main;name=freertos \
    git://github.com/nxp-qoriq/la93xx_host_sw.git;protocol=https;branch=main;name=hostsw;destsuffix=hostsw \
    git://github.com/myriadrf/LimeSuiteNG.git;protocol=https;branch=limesdr-micro;name=lime;destsuffix=LimeSuiteNG \
"

SRCREV_freertos = "${AUTOREV}"
SRCREV_hostsw = "${AUTOREV}"
SRCREV_lime = "${AUTOREV}"
SRCREV_FORMAT = "freertos_hostsw_lime"

COMPATIBLE_MACHINE = "(imx95-navq.*)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

FW_SRC_DIR = "${S}/Demo/CORTEX_M4_NXP_LA9310_GCC"
ARMGCC_DIR = "${STAGING_DIR_NATIVE}${libexecdir}/gcc-arm-none-eabi-6-2017q2"
LA9310_HOST_SW_UAPI = "${UNPACKDIR}/hostsw/uapi"

# cmake configure is driven by build_release.sh; nothing to do here.
do_configure[noexec] = "1"

do_compile() {
    # Do not leak Yocto target build flags into the bare-metal cross build;
    # armgcc.cmake supplies its own flags.
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS CC CXX LD AR AS OBJCOPY

    export ARMGCC_DIR="${ARMGCC_DIR}"
    export LA9310_HOST_SW_UAPI="${LA9310_HOST_SW_UAPI}"

    cd ${FW_SRC_DIR}
    # build_release.sh drives cmake directly. Two tweaks for the modern
    # cmake-native + GCC 6.3 toolchain: (1) the bundled CMakeLists uses an
    # ancient cmake_minimum_required that modern cmake rejects; (2) it sets
    # CMAKE_COMPILE_WARNING_AS_ERROR ON, but GCC 6.3 lacks some -Wno-* options
    # the code passes (e.g. -Wno-address-of-packed-member), which then error out.
    if ! grep -q 'compile-no-warning-as-error' build_release.sh; then
        sed -i 's|^cmake |cmake -DCMAKE_POLICY_VERSION_MINIMUM=3.5 --compile-no-warning-as-error |' build_release.sh
    fi
    ./build_release.sh -m pcie -t lime_sdr_micro -f LA9310_DFE_APP=ON -b release
}

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware
    install -m 0644 ${FW_SRC_DIR}/release/la9310.bin \
        ${D}${nonarch_base_libdir}/firmware/la9310_dfe.bin
}

FILES:${PN} = "${nonarch_base_libdir}/firmware/la9310_dfe.bin"

# Raw firmware blob for a foreign core: skip arch/ELF-oriented QA. The GCC 6.3
# toolchain lacks -ffile-prefix-map, so __FILE__/assert paths bake a TMPDIR
# reference into the blob (harmless for a firmware image) -> skip buildpaths too.
INSANE_SKIP:${PN} = "arch buildpaths"
