# A desktop image with an Desktop rootfs
#
# Note that we have a tight dependency to ubuntu-base
# and that we cannot just install arbitrary Yocto packages to avoid
# rootfs pollution or destruction.
PV = "${@d.getVar('PREFERRED_VERSION_ubuntu-base', True) or '1.0'}"

require fsl-image-common.inc

# This must be added first as it provides the foundation for
# subsequent modifications to the rootfs
IMAGE_INSTALL += "\
	ubuntu-base \
	ubuntu-base-dev \
	ubuntu-base-dbg \
	ubuntu-base-doc \
"

# Include kernel source
IMAGE_INSTALL += "\
	kernel-dev \
	kernel-devsrc \
	kernel-modules \
"

IMAGE_INSTALL += "\
	firmwared \
	udev-extraconf \
	packagegroup-fsl-gstreamer1.0-full \
"

IMAGE_INSTALL:append:imx8mpnavq = " \
	libopenvx-imx \
	libopenvx-imx-dev \
	libnn-imx \
	tensorflow-lite \
	tensorflow-lite-vx-delegate \
	"

# Mali-G310 needs its CSF firmware to run under Panthor: at probe the driver
# reads the GPU arch and requests arm/mali/arch10.12/mali_csffw.bin. Without it
# request_firmware fails and panthor waits out the 60s sysfs fallback, which also
# stalls systemd-modules-load (panthor.conf autoload) and pushes boot past 90s.
# meta-imx names this exact package in MACHINE_EXTRA_RDEPENDS:imxmali, but this
# Ubuntu-base image only installs explicit IMAGE_INSTALL, so it never arrived.
# Shipping it lets Panthor bind (renderD129 appears, Mesa uses the GPU instead of
# llvmpipe) and drops boot to ~14s.
IMAGE_INSTALL:append:imx95-navq = " \
	rpmsgexport \
	rpmsgfs-remotedirs \
	rpmsgfs-server \
	autoivnsw-sja1110-linux \
	firmware-sja1110 \
	la93xx-host-sw \
	la9310-firmware \
	linux-firmware-mali-csffw-arch1012 \
	panvk-layer \
    enetc-vsi-handoff-mcore \
	"

APTGET_EXTRA_PACKAGES += "\
	ntpdate patchelf \
	python3-numpy \
	python3-pil \
	python3-pip \
	vulkan-tools \
	iw \
	rfkill \
	wireless-tools \
"

##############################################################################
# NOTE: We cannot install arbitrary Yocto packages as they will
# conflict with the content of the prebuilt Desktop rootfs and pull
# in dependencies that may break the rootfs.
# Any package addition needs to be carefully evaluated with respect
# to the final image that we build.
##############################################################################

# GPU driver
G2D_SAMPLES              = ""
G2D_SAMPLES:imxgpu2d     = "imx-g2d-samples"
G2D_SAMPLES:mx93-nxp-bsp = "imx-g2d-samples"
G2D_SAMPLES:mx943-nxp-bsp = "imx-g2d-samples"

IMAGE_INSTALL:append:imx95-navq = " \
	litert \
	litert-dev \
	onnxruntime-dev \
	onnxruntime-tests \
	pytorch \
	tensorflow-lite \
	tensorflow-lite-dev \
	neutron \
	litert-neutron-delegate \
	tensorflow-lite-neutron-delegate \
	imx-nnstreamer-examples \
	nnstreamer \
	nnstreamer-protobuf \
	nnstreamer-python3 \
	nnstreamer-query \
	nnstreamer-tensorflow-lite \
	gstreamer1.0-dev \
	gst-shark \
	libcamera \
	libcamera-gst \
	libcamera-pycamera \
	neo-ipa-uguzzi \
	weston \
	xwayland \
	weston-xwayland \
	patrace \
	${G2D_SAMPLES} \
	reboot-to-fastboot \
	"

# Don’t let pseudo track the transient lock file
PSEUDO_IGNORE_PATHS:append = ",${IMAGE_ROOTFS}/etc/.pwd.lock"

# Clean up the lock if it ends up in the rootfs
remove_pwd_lock () {
    rm -f ${IMAGE_ROOTFS}/etc/.pwd.lock || true
}

PACKAGE_EXCLUDE = "libgles3-imx-dev libegl-imx-dev libc6-dev"

ROOTFS_POSTPROCESS_COMMAND += "write_build_info_to_rootfs;remove_pwd_lock;"

write_build_info_to_rootfs() {
    echo "Generating build info..."

    mkdir -p ${IMAGE_ROOTFS}/etc

    # Timestamp
    date -u +"%Y-%m-%dT%H:%M:%SZ" > ${IMAGE_ROOTFS}/etc/build-info.txt

    # Build Configuration
    echo "" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    echo "Build Configuration:" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    echo "BB_VERSION           = \"${BB_VERSION}\"" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    echo "BUILD_SYS            = \"${BUILD_SYS}\"" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    echo "NATIVELSBSTRING      = \"${NATIVELSBSTRING}\"" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    echo "TARGET_SYS           = \"${TARGET_SYS}\"" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    echo "MACHINE              = \"${MACHINE}\"" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    echo "DISTRO               = \"${DISTRO}\"" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    echo "DISTRO_VERSION       = \"${DISTRO_VERSION}\"" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    echo "TUNE_FEATURES        = \"${TUNE_FEATURES}\"" >> ${IMAGE_ROOTFS}/etc/build-info.txt

    # Host Linux Distribution
    echo "" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    echo "Build Host OS:" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        echo "${NAME} ${VERSION}" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    else
        echo "Unknown" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    fi

    # Layers with Git info
    echo "" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    echo "Layers:" >> ${IMAGE_ROOTFS}/etc/build-info.txt
    for layer in ${BBLAYERS}; do
        if [ -d "$layer/.git" ]; then
            name=$(basename $layer)
            branch=$(git -C $layer rev-parse --abbrev-ref HEAD 2>/dev/null || echo "unknown")
            commit=$(git -C $layer rev-parse HEAD 2>/dev/null || echo "unknown")
            dirty=$(git -C $layer diff --quiet || echo "-dirty")
            echo "$name = \"$branch:$commit${dirty}\"" >> ${IMAGE_ROOTFS}/etc/build-info.txt
        else
            echo "$(basename $layer)" >> ${IMAGE_ROOTFS}/etc/build-info.txt
        fi
    done

}
