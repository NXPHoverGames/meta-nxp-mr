SUMMARY = "GNU Arm Embedded Toolchain 6-2017-q2-update (arm-none-eabi)"
DESCRIPTION = "Prebuilt AArch32 bare-metal (arm-none-eabi) GCC toolchain, \
version 6-2017-q2-update. This is the exact toolchain specified by the NXP \
LimeSDR-Micro porting guide (section 6.1.2) for building the LA9310 FreeRTOS \
dfe_app firmware. Provided as a native build tool only."
HOMEPAGE = "https://developer.arm.com/downloads/-/gnu-rm"

LICENSE = "GPL-3.0-with-GCC-exception & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://share/doc/gcc-arm-none-eabi/license.txt;md5=c224e429f53a1a6ce70bf8986ea2990b"

# Prebuilt x86_64 Linux host binaries.
COMPATIBLE_HOST = "x86_64.*-linux"

INHIBIT_DEFAULT_DEPS = "1"

SRC_URI = "https://developer.arm.com/-/media/Files/downloads/gnu-rm/6-2017q2/gcc-arm-none-eabi-6-2017-q2-update-linux.tar.bz2"
SRC_URI[sha256sum] = "e68e4b2fe348ecb567c27985355dff75b65319a0f6595d44a18a8c5e05887cc3"

S = "${UNPACKDIR}/gcc-arm-none-eabi-6-2017-q2-update"

# Install to a stable, versionless path so consumers can reference ARMGCC_DIR
# without tracking PV. Symlink the executables into bindir so the toolchain is
# also on PATH.
ARMGCC_INSTALL_DIR = "${libexecdir}/${BPN}"

do_install() {
    install -d ${D}${ARMGCC_INSTALL_DIR}
    cp -R ${S}/. ${D}${ARMGCC_INSTALL_DIR}

    install -d ${D}${bindir}
    for f in ${D}${ARMGCC_INSTALL_DIR}/bin/*; do
        ln -rs $f ${D}${bindir}/$(basename $f)
    done
}

FILES:${PN} = "${libexecdir} ${bindir}"

# Prebuilt binaries: skip QA that does not apply to a third-party toolchain.
INSANE_SKIP:${PN} = "already-stripped libdir staticdev file-rdeps arch dev-so"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
SKIP_FILEDEPS = "1"
PRIVATE_LIBS = "libgcc_s.so.1 libstdc++.so.6"

BBCLASSEXTEND = "native"
