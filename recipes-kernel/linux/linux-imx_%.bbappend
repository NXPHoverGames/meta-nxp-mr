FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
         file://0001-net-phy-nxp-c45-tja11xx-add-rev-rmii-support.patch \
         file://0002-net-fec-add-rev-rmii-support.patch \
         file://0003-arm64-dts-imx8mp-navq-Fix-TJA1103-phy.patch \
         file://0004-nxp-sr1xx-uwb-driver.patch \
         file://0005-imx8mpnavq-enable-sr1xx-spi-driver-in-dts.patch \
         file://0006-ov5645tn-driver-for-navq.patch \
         file://uwb.cfg \
"

do_configure:append () {
    ${S}/scripts/kconfig/merge_config.sh -m -O ${B} ${B}/.config $(ls ${WORKDIR}/*.cfg)

    if [ ! -z "${LOCALVERSION}" ]; then
        echo "CONFIG_LOCALVERSION="\"${LOCALVERSION}\" >> ${B}/.config
    fi
}

