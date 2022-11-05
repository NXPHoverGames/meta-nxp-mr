SUMMARY = "A flexible distro installer"
LICENSE = "BSD"

S = "${WORKDIR}"
do_deploy[nostamp] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

INHIBIT_DEFAULT_DEPS = "1"
RDEPENDS:${PN} += "bash"

do_install () {
    install -d ${D}/${bindir}
    install ${TOPDIR}/../sources/meta-nxp-desktop/scripts/flex-installer ${D}/${bindir}
}

do_deploy () {
    mkdir -p ${DEPLOY_DIR_IMAGE}
    install -m 644 ${TOPDIR}/../sources/meta-nxp-desktop/scripts/flex-installer ${DEPLOY_DIR_IMAGE}/
}

addtask deploy after do_install before do_build

FILES_${PN} += "${bindir}"
INSANE_SKIP_${PN} += "arch already-stripped"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

COMPATIBLE_MACHINE = "(qoriq)"
