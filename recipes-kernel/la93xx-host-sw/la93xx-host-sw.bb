SUMMARY = "NXP LA9310 host software kernel modules (shiva)"
DESCRIPTION = "Out-of-tree LA9310 PCIe host driver kernel modules (la9310shiva \
and la9310demo), following the NXP LimeSDR-Micro porting guide. The optional \
RFNM/SDR modules (IMX_SDR, IMX_RFLIME) are not built by default. Userspace \
libraries, firmware and dpdk dfe_app are not built by this recipe."
HOMEPAGE = "https://github.com/nxp-qoriq/la93xx_host_sw"

# Kernel components are dual BSD-3-Clause & GPL-2.0 licensed.
LICENSE = "GPL-2.0-only & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://license/bsd-3-clause.txt;md5=0f00d99239d922ffd13cabef83b33444"

inherit module

# la93xx_host_sw carries the driver sources; la93xx_freertos provides the shared
# common_headers/ required by the kernel driver (LA9310_COMMON_HEADERS).
SRC_URI = " \
    git://github.com/nxp-qoriq/la93xx_host_sw.git;protocol=https;branch=main;name=hostsw \
    git://github.com/nxp-qoriq/la93xx_freertos.git;protocol=https;branch=main;name=freertos;destsuffix=freertos \
    file://0001-la9310shiva-use-eventfd_fget-for-kernel-6.12.patch \
"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRCREV_hostsw = "${AUTOREV}"
SRCREV_freertos = "${AUTOREV}"
SRCREV_FORMAT = "hostsw_freertos"

LA9310_COMMON_HEADERS = "${UNPACKDIR}/freertos/common_headers"

# The freertos checkout (used only for common_headers) is outside ${S}, so it is
# not covered by the default debug prefix maps; add it to avoid TMPDIR paths
# leaking into the module debug info (buildpaths QA).
DEBUG_PREFIX_MAP:append = " -ffile-prefix-map=${UNPACKDIR}/freertos=/usr/src/debug/${BPN}/${PV}"

# LA9310 is a PCIe endpoint targeted at the i.MX95 NavQ boards.
COMPATIBLE_MACHINE = "(imx95-navq.*)"

# The top-level Makefile "clean" recurses into the firmware submodule (not
# fetched here) and has no clean rule, so skip the default do_configure clean.
CLEANBROKEN = "1"

# The kernel-driver build follows the LimeSDR-Micro porting guide, which runs a
# plain "make" (shiva + demo). The RFNM-ecosystem modules (IMX_SDR / IMX_RFLIME:
# la9310sdr, sdr_lime, ...) are NOT part of the LimeSDR-Micro-on-NXP flow - RF /
# Lime control there is handled by the FreeRTOS dfe_app firmware and the dpdk
# dfe_app userspace, not by kernel modules. They also require RFNM kernel headers
# (linux/sdr-shared.h, linux/rfnm-api.h) that are not present in this BSP kernel.
# If ever needed, they can be turned on with:
#   LA9310_EXTRA_MAKE = "IMX_SDR=1 IMX_RFLIME=1"
LA9310_EXTRA_MAKE ?= ""

EXTRA_OEMAKE = " \
    KERNEL_DIR=${STAGING_KERNEL_BUILDDIR} \
    KERNEL_SRC=${STAGING_KERNEL_DIR} \
    KERNEL_VERSION=${KERNEL_VERSION} \
    LA9310_COMMON_HEADERS=${LA9310_COMMON_HEADERS} \
    COMMON_DIR=${S}/common \
    UAPI_DIR=${S}/uapi \
    LA9310_DRV_HEADER_DIR=${S}/kernel_driver/la9310shiva \
    CONFIG_ENABLE_FLOAT_BYPASS=y \
    ${LA9310_EXTRA_MAKE} \
"

do_compile() {
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    # Version macros are normally derived from "git describe" in the top-level
    # Makefile; provide fixed, properly quoted values since we build the
    # kernel_driver subdir directly (embedded escaped quotes -> C string).
    oe_runmake -C ${S}/kernel_driver \
        CC="${KERNEL_CC}" LD="${KERNEL_LD}" AR="${KERNEL_AR}" \
        OBJCOPY="${KERNEL_OBJCOPY}" STRIP="${KERNEL_STRIP}" \
        O=${STAGING_KERNEL_BUILDDIR} \
        VERSION_STRING='\"v3.x\"' GIT_VERSION='\"v3.x\"'
}

do_install() {
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra
    find ${S}/kernel_driver -name '*.ko' -exec \
        install -m 0644 {} ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/ \;
}

RPROVIDES:${PN} += "kernel-module-la9310shiva"
