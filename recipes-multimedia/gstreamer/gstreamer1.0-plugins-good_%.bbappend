FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://0001-request-for-2-more-buffer-for-better-performance.patch \
    file://0001-LF-9558-gtkgstbasewidget-default-set-ignore-alpha-to.patch \
"

PACKAGECONFIG:append = " gtk"
