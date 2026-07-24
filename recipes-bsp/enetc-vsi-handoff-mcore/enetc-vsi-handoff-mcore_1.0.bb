SUMMARY = "Hand an ENETC VSI (VF) off to the i.MX95 Cortex-M7 (M-core)"
DESCRIPTION = "Provisions an ENETC VF on the A55 (SR-IOV, BME, MSI-X, trust) so \
the M7 Zephyr NETC-VSI driver can drive Ethernet. Re-runs every boot."
HOMEPAGE = "https://github.com/NXP-Robotics/meta-nxp-mr"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = " \
    file://enetc-vsi-handoff-mcore \
    file://enetc-vsi-handoff-mcore.default \
    file://enetc-vsi-handoff-mcore.service \
"

inherit systemd
SYSTEMD_SERVICE:${PN} = "enetc-vsi-handoff-mcore.service"
# Auto-enable so it runs on every boot without manual `systemctl enable`.
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/enetc-vsi-handoff-mcore ${D}${sbindir}/enetc-vsi-handoff-mcore

    install -d ${D}${sysconfdir}/default
    install -m 0644 ${UNPACKDIR}/enetc-vsi-handoff-mcore.default ${D}${sysconfdir}/default/enetc-vsi-handoff-mcore

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/enetc-vsi-handoff-mcore.service ${D}${systemd_system_unitdir}/enetc-vsi-handoff-mcore.service
}

FILES:${PN} += "${systemd_system_unitdir}/enetc-vsi-handoff-mcore.service"
CONFFILES:${PN} = "${sysconfdir}/default/enetc-vsi-handoff-mcore"

COMPATIBLE_MACHINE = "(imx95-navqdesktop|imx95)"
