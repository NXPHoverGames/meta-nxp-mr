FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
         file://cpufreq.cfg \
         file://usbserial.cfg \
         file://fs_sec.cfg \
"

SRC_URI:append:imx95-navq = " \
         file://rpmsg.cfg \
         file://mali.cfg \
         file://input.cfg \
         file://video-dummy.cfg \
         file://panel-ili9881c.cfg \
         file://cam.cfg \
         file://sfp.cfg \
         file://p3h.cfg \
         file://lt8912b.cfg \
"

SRC_URI:append:imx8mpnavq = " \
         file://0001-net-phy-nxp-c45-tja11xx-add-rev-rmii-support.patch \
         file://0002-net-fec-add-rev-rmii-support.patch \
         file://0003-arm64-dts-imx8mp-navq-Fix-TJA1103-phy.patch \
         file://0004-nxp-sr1xx-uwb-driver.patch \
         file://0005-imx8mpnavq-enable-sr1xx-spi-driver-in-dts.patch \
         file://0006-ov5645tn-driver-for-navq.patch \
         file://uwb.cfg \
"

SRCBRANCH = "imx95-navq-lf-6.18.y-Q2"
SRCREV = "${AUTOREV}"
LINUX_IMX_SRC = "git://git@github.com/NXP-Robotics/linux-imx.git;protocol=https;branch=${SRCBRANCH}"
SRC_URI = "${LINUX_IMX_SRC}"

do_configure:append () {
    ${S}/scripts/kconfig/merge_config.sh -m -O ${B} ${B}/.config $(ls ${UNPACKDIR}/*.cfg)

    if [ ! -z "${LOCALVERSION}" ]; then
        echo "CONFIG_LOCALVERSION=\"\"" >> ${B}/.config
    fi
}

