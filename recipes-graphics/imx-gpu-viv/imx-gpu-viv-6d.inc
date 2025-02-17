# Copyright (C) 2012-2016 Freescale Semiconductor
# Copyright (C) 2012-2018 O.S. Systems Software LTDA.
# Copyright 2017-2021 NXP
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "GPU driver and apps for i.MX"
SECTION = "libs"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=80c0478f4339af024519b3723023fe28"

DEPENDS += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', \
       bb.utils.contains('DISTRO_FEATURES',     'x11', 'virtual/libx11 libxdamage libxext libxfixes', \
                                                       '', d), d)} \
"
DEPENDS += " \
    ${@bb.utils.contains('DISTRO_FEATURES',     'x11', 'libxcb libxshmfence libxxf86vm', \
                                                       '', d)} \
"
DEPENDS:append:imxdrm = " libdrm wayland"
DEPENDS:append:mx8-nxp-bsp = " patchelf-native"

# imx-gpu-viv does not provide everything it needs to for virtual/libgl
# on x11 backend or on Wayland backend with XWayland support.
# We depend on mesa to fill in what is missing.
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'mesa', '', d)}"

EXTRA_PROVIDES = " \
    ${PROVIDES_OPENCL} \
    ${PROVIDES_OPENVX} \
"
EXTRA_PROVIDES:append:imxgpu3d = " \
    virtual/libgl \
    virtual/libgles1 \
    virtual/libgles2 \
"
EXTRA_PROVIDES:append:mx8-nxp-bsp = " \
    virtual/libgbm \
"
PROVIDES_OPENVX                  = ""
PROVIDES_OPENVX:mx8-nxp-bsp      = "virtual/libopenvx"
PROVIDES_OPENVX:mx8mm-nxp-bsp    = ""

PROVIDES_OPENCL = " \
    opencl-headers \
    opencl-clhpp \
    opencl-icd-loader \
    virtual/opencl-icd \
"
PROVIDES_OPENCL:mx8mm-nxp-bsp = ""

PROVIDES += " \
    imx-gpu-viv \
    libgal-imx \
    virtual/egl \
    virtual/libopenvg \
    ${EXTRA_PROVIDES} \
"

DEPENDS:remove = "mesa"
PROVIDES:remove = "virtual/libgl"

RPROVIDES:${PN}:imxgpu3d += "imx-gpu-viv"

PE = "1"

inherit fsl-eula-unpack features_check

REQUIRED_DISTRO_FEATURES:mx8-nxp-bsp = "wayland"

SRC_URI = "${FSL_BIN_MIRROR}/${BPN}-${PV}.bin;fsl-eula=true"

# Note : If you add a package here, to prevent a naming conflict see the python_anonymous() futher below
IMX_PACKAGES_GBM     = ""
IMX_PACKAGES_GBM:mx8-nxp-bsp = "libgbm-imx libgbm-imx-dev"
PACKAGES =+ " \
	libgles1-imx libgles1-imx-dev \
	libgles2-imx libgles2-imx-dev \
	libgles3-imx-dev \
	libglslc-imx libglslc-imx-dev \
	${PACKAGES_OPENCL} \
	libopenvg-imx libopenvg-imx-dev \
	libvdk-imx libvdk-imx-dev \
	libegl-imx libegl-imx-dev \
	libgal-imx libgal-imx-dev \
	libvivante-dri-imx \
	libvsc-imx \
	${IMX_PACKAGES_GBM} \
	libwayland-viv-imx libwayland-viv-imx-dev \
	libgc-wayland-protocol-imx libgc-wayland-protocol-imx-dev \
	imx-gpu-viv-tools \
	${PACKAGES_VULKAN} \
	${PACKAGES_OPENVX} \
    libnn-imx \
"
PACKAGES_OPENCL               = "libclc-imx libclc-imx-dev libopencl-imx libopencl-imx-dev"
PACKAGES_OPENCL:mx8mm-nxp-bsp = ""

PACKAGES_OPENVX = \
    "${@bb.utils.contains("PROVIDES_OPENVX", "virtual/libopenvx", \
        "libopenvx-imx libopenvx-imx-dev", "", d)}"

PACKAGES_VULKAN               = ""
PACKAGES_VULKAN:imxvulkan     = "libvulkan-imx libvulkan-imx-dev"
PACKAGES_VULKAN:mx8mm-nxp-bsp = "libvulkan-imx libvulkan-imx-dev"

python __anonymous () {
        has_vivante_kernel_driver_support = (d.getVar('MACHINE_HAS_VIVANTE_KERNEL_DRIVER_SUPPORT') or '0')
        if has_vivante_kernel_driver_support != '1':
                raise bb.parse.SkipPackage('The kernel of machine needs to have Vivante kernel driver support for this recipe to be used.')
}

USE_X11 = "${@bb.utils.contains("DISTRO_FEATURES", "x11", "yes", "no", d)}"
USE_WL = "${@bb.utils.contains("DISTRO_FEATURES", "wayland", "yes", "no", d)}"

# The packaged binaries have been stripped of debug info, so disable
# operations accordingly.
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

# FIXME: The provided binary doesn't provide soname. If in future BSP
# release the libraries are fixed, we can drop this hack.
REALSOLIBS := "${SOLIBS}"
SOLIBS = "${SOLIBSDEV}"

python __anonymous() {
    # FIXME: All binaries lack GNU_HASH in elf binary but as we don't have
    # the source we cannot fix it. Disable the insane check for now.
    packages = d.getVar('PACKAGES').split()
    for p in packages:
        d.appendVar("INSANE_SKIP:%s" % p, " ldflags")

    # For the packages that make up the OpenGL interfaces, inject variables so that
    # they don't get Debian-renamed (which would remove the -imx suffix).
    for p in (("libegl", "libegl1"), ("libgl", "libgl1"),
              ("libgles1", "libglesv1-cm1"), ("libgles2", "libglesv2-2"),
              ("libgles3",), ("libgbm",)):
        fullp = p[0] + "-imx"
        pkgs = "".join(' %s' % i for i in p)
        d.setVar("DEBIAN_NOAUTONAME:" + fullp, "1")
        d.appendVar("RREPLACES:" + fullp, pkgs)
        d.appendVar("RPROVIDES:" + fullp, pkgs)
        d.appendVar("RCONFLICTS:" + fullp, pkgs)

        # For -dev, the first element is both the Debian and original name
        fullp += "-dev"
        pkgs = p[0] + "-dev"
        d.setVar("DEBIAN_NOAUTONAME:" + fullp, "1")
        d.appendVar("RREPLACES:" + fullp, pkgs)
        d.appendVar("RPROVIDES:" + fullp, pkgs)
        d.appendVar("RCONFLICTS:" + fullp, pkgs)
}

IS_MX6SL = "0"
IS_MX6SL:mx6sl-nxp-bsp = "1"

IS_MX8 = "0"
IS_MX8:mx8-nxp-bsp = "1"

PACKAGE_FP_TYPE = "hardfp"

HAS_GBM     = "false"
HAS_GBM:mx8-nxp-bsp = "true"

GLES3_HEADER_REMOVALS        = "gl31.h gl32.h"
GLES3_HEADER_REMOVALS:mx8m-nxp-bsp   = "gl32.h"
GLES3_HEADER_REMOVALS:mx8mm-nxp-bsp  = "gl31.h gl32.h"
GLES3_HEADER_REMOVALS:mx8qxp-nxp-bsp = ""
GLES3_HEADER_REMOVALS:mx8qm-nxp-bsp  = ""

VXC_BINARY_REMOVALS          = "libNNGPUBinary-*.so libNNVXCBinary-*.so libOvx12VXCBinary-*.so"

VXC_BINARY_INSTALLS          = ""
VXC_BINARY_INSTALLS:mx8qxp-nxp-bsp   = "libNNGPUBinary-lite.so"
VXC_BINARY_INSTALLS:mx8qm-nxp-bsp    = "libNNGPUBinary-evis.so libNNVXCBinary-evis.so libOvx12VXCBinary-evis.so"
VXC_BINARY_INSTALLS:mx8mp-nxp-bsp    = "libNNGPUBinary-evis2.so libNNVXCBinary-evis2.so libOvx12VXCBinary-evis2.so libNNGPUBinary-ulite.so"
VXC_BINARY_INSTALLS:mx8mq-nxp-bsp    = "libNNGPUBinary-lite.so"
VXC_BINARY_INSTALLS:mx8mn-nxp-bsp    = "libNNGPUBinary-ulite.so"

do_install () {
    install -d ${D}${libdir}
    install -d ${D}${includedir}
    install -d ${D}${bindir}

    cp -P ${S}/gpu-core/usr/lib/*.so* ${D}${libdir}
    cp -r ${S}/gpu-core/usr/include/* ${D}${includedir}
    cp -r ${S}/gpu-tools/gmem-info/usr/bin/* ${D}${bindir}

    # Use vulkan header from vulkan-headers recipe to support vkmark
    rm -rf ${D}${includedir}/vulkan/

    install -d ${D}${libdir}/pkgconfig
    if ${HAS_GBM}; then
        install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/gbm.pc ${D}${libdir}/pkgconfig/gbm.pc
    fi

    # The preference order, based in DISTRO_FEATURES, is Wayland (with or without X11), X11 and fb
    if [ "${USE_WL}" = "yes" ]; then

        backend=wayland

        install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/egl_wayland.pc ${D}${libdir}/pkgconfig/egl.pc
        install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/glesv1_cm.pc ${D}${libdir}/pkgconfig/glesv1_cm.pc
        install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/glesv2.pc ${D}${libdir}/pkgconfig/glesv2.pc

    elif [ "${USE_X11}" = "yes" ]; then

        cp -r ${S}/gpu-core/usr/lib/dri ${D}${libdir}

        backend=x11

        install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/gl_x11.pc ${D}${libdir}/pkgconfig/gl.pc
        install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/egl_x11.pc ${D}${libdir}/pkgconfig/egl.pc
        install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/glesv1_cm_x11.pc ${D}${libdir}/pkgconfig/glesv1_cm.pc
        install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/glesv2_x11.pc ${D}${libdir}/pkgconfig/glesv2.pc
        install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/vg_x11.pc ${D}${libdir}/pkgconfig/vg.pc

    else
        install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/glesv1_cm.pc ${D}${libdir}/pkgconfig/glesv1_cm.pc
        install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/glesv2.pc ${D}${libdir}/pkgconfig/glesv2.pc
        install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/vg.pc ${D}${libdir}/pkgconfig/vg.pc

        # Regular framebuffer
        install -m 0644 ${S}/gpu-core/usr/lib/pkgconfig/egl_linuxfb.pc ${D}${libdir}/pkgconfig/egl.pc

        backend=fb

    fi

    if [ -z "${PACKAGES_OPENCL}" ]; then
        set -f
        for f in ${FILES:libopencl-imx} ${FILES:libopencl-imx-dev} ${FILES:libclc-imx}; do
            set +f
            rm -rf ${D}$f
        done
        rm -rf ${D}/opt/viv_samples/cl11
    fi

    if [ -z "${PACKAGES_OPENVX}" ]; then
        set -f
        for f in ${FILES:libopenvx-imx} ${FILES:libopenvx-imx-dev}; do
            set +f
            rm -rf ${D}$f
        done
    fi

    # Install Vendor ICDs for OpenCL's installable client driver loader (ICDs Loader)
    install -d ${D}${sysconfdir}/OpenCL/vendors/
    install -m 0644 ${S}/gpu-core/etc/Vivante.icd ${D}${sysconfdir}/OpenCL/vendors/Vivante.icd

    # Handle backend specific drivers
    cp -r ${S}/gpu-core/usr/lib/${backend}/* ${D}${libdir}
    if [ "${IS_MX8}" = "1" ]; then
        ln -sf libvulkan_VSI.so ${D}${libdir}/libvulkan.so.1
        ln -sf libvulkan.so.1 ${D}${libdir}/libvulkan.so
    fi
    for header in ${GLES3_HEADER_REMOVALS}; do
        rm -f ${D}${includedir}/GLES3/${header}
    done

    for vxcbinary in ${VXC_BINARY_REMOVALS}; do
        rm -f ${D}${libdir}/${vxcbinary}
    done

    for vxcbinary in ${VXC_BINARY_INSTALLS}; do
        cp -P ${S}/gpu-core/usr/lib/${vxcbinary} ${D}${libdir}
    done

    # skip packaging wayland libraries if no support is requested
    if [ "${USE_WL}" = "no" ]; then
        find ${D}${libdir} -name "libgc_wayland_protocol.*" -exec rm '{}' ';'
        find ${D}${libdir} -name "libwayland-viv.*" -exec rm '{}' ';'
    fi

    # FIXME: MX6SL does not have 3D support; hack it for now
    if [ "${IS_MX6SL}" = "1" ]; then
        rm -rf ${D}${libdir}/libCLC* ${D}${includedir}/CL \
               \
               ${D}${libdir}/libGL* ${D}${includedir}/GL* ${D}${libdir}/pkgconfig/gl.pc \
               \
               ${D}${libdir}/libGLES* ${D}${libdir}/pkgconfig/gles*.pc \
               \
               ${D}${libdir}/libOpenCL* ${D}${includedir}/CL \
               \
               ${D}${libdir}/libOpenVG.3d.so \
               \
               ${D}${libdir}/libVivanteOpenCL.so \
               \
               ${D}/opt/viv_samples/vdk \
               ${D}/opt/viv_samples/es20 ${D}/opt/viv_samples/cl11

        ln -sf libOpenVG.2d.so ${D}${libdir}/libOpenVG.so
    fi

    find ${D}${libdir} -type f -exec chmod 644 {} \;
    find ${D}${includedir} -type f -exec chmod 644 {} \;

    chown -R root:root "${D}"
}

ALLOW_EMPTY:${PN} = "1"
INSANE_SKIP:libvulkan-imx += "dev-deps"

FILES:libclc-imx = "${libdir}/libCLC${SOLIBS} ${includedir}/CL/cl_viv_vx_ext.h"
FILES:libclc-imx-dev = "${includedir}/CL ${libdir}/libCLC${SOLIBSDEV}"

# libEGL.so is used by some demo apps from Freescale
INSANE_SKIP:libegl-imx += "dev-so"
FILES:libegl-imx = "${libdir}/libEGL${REALSOLIBS} ${libdir}/libEGL${SOLIBSDEV} "
FILES:libegl-imx-dev = "${includedir}/EGL ${includedir}/KHR ${libdir}/pkgconfig/egl.pc"

FILES:libgal-imx = "${libdir}/libGAL${SOLIBS} ${libdir}/libGAL_egl${SOLIBS}"
FILES:libgal-imx-dev = "${libdir}/libGAL${SOLIBSDEV} ${includedir}/HAL"
RDEPENDS:libgal-imx += "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'valgrind', '', d)}"
RPROVIDES:libgal-imx += "libgal-imx"
INSANE_SKIP:libgal-imx += "build-deps"

FILES:libvsc-imx = "${libdir}/libVSC${SOLIBS}"

FILES:libgbm-imx:mx8-nxp-bsp = "${libdir}/libgbm${REALSOLIBS} ${libdir}/libgbm${SOLIBSDEV} ${libdir}/libgbm_viv${SOLIBSDEV}"
FILES:libgbm-imx-dev:mx8-nxp-bsp = "${libdir}/pkgconfig/gbm.pc ${includedir}/gbm.h"
RDEPENDS:libgbm-imx:append:mx8-nxp-bsp = " libdrm"
INSANE_SKIP:libgbm-imx += "dev-so"

FILES:libvulkan-imx = "${libdir}/libvulkan${SOLIBS}.1 ${libdir}/libvulkan${SOLIBS} ${libdir}/libvulkan_VSI${REALSOLIBS} ${libdir}/libSPIRV_viv${SOLIBS}"
FILES:libvulkan-imx-dev = "${includedir}/vulkan ${libdir}/libvulkan${SOLIBSDEV}"
INSANE_SKIP:libvulkan-imx += "dev-so"

FILES:libopenvx-imx = "${libdir}/libOpenVX${SOLIBS} ${libdir}/libOpenVX${REALSOLIBS} ${libdir}/libOvx*${SOLIBS} ${libdir}/libOpenVXC${SOLIBS} ${libdir}/libOpenVXU${SOLIBS} ${libdir}/libArchModelSw${SOLIBS}"
FILES:libopenvx-imx-dev = "${includedir}/VX ${libdir}/libOpenVX${SOLIBSDEV}"
RDEPENDS:libopenvx-imx = "libnn-imx"
INSANE_SKIP:libopenvx-imx += "dev-so dev-deps"

FILES:libgl-imx = "${libdir}/libGL${REALSOLIBS}"
FILES:libgl-imx-dev = "${libdir}/libGL${SOLIBSDEV} ${includedir}/GL"
INSANE_SKIP:libgl-imx += "file-rdeps"

# libEGL needs to open libGLESv1.so
INSANE_SKIP:libgles1-imx += "dev-so"
FILES:libgles1-imx = "${libdir}/libGLESv1*${REALSOLIBS} ${libdir}/libGLESv1*${SOLIBS} ${libdir}/libGLES_*${REALSOLIBS} ${libdir}/libGLES_*${SOLIBS}"
FILES:libgles1-imx-dev = "${includedir}/GLES ${libdir}/libGLESv1*${SOLIBS} ${libdir}/libGLES_*${SOLIBSDEV} ${libdir}/pkgconfig/glesv1_cm.pc"
RPROVIDES:libgles1-imx = "libgles-imx"
RPROVIDES:libgles1-imx-dev = "libgles-imx-dev"

# libEGL needs to open libGLESv2.so
INSANE_SKIP:libgles2-imx += "dev-so"
FILES:libgles2-imx = "${libdir}/libGLESv2${REALSOLIBS} ${libdir}/libGLESv2${SOLIBS}"
FILES:libgles2-imx-dev = "${includedir}/GLES2 ${libdir}/libGLESv2${SOLIBSDEV} ${libdir}/pkgconfig/glesv2.pc"
RDEPENDS:libgles2-imx = "libglslc-imx"

FILES:libgles3-imx-dev = "${includedir}/GLES3"
# as long as there is no libgles3: ship libgles3-dev along with
# libgles2-dev - otherwise GLES3 headers have to be added manually
RDEPENDS:libgles2-imx-dev += "libgles3-imx-dev"

FILES:libglslc-imx = "${libdir}/libGLSLC${SOLIBS}"
FILES:libglslc-imx-dev = "${includedir}/CL ${libdir}/libGLSLC${SOLIBSDEV}"

FILES:libopencl-imx = "${libdir}/libOpenCL${SOLIBS} \
                       ${libdir}/libOpenCL${REALSOLIBS} \
                       ${libdir}/libVivanteOpenCL${SOLIBS} \
                       ${libdir}/libLLVM_viv${SOLIBS} \
                       ${sysconfdir}/OpenCL/vendors/Vivante.icd"
FILES:libopencl-imx-dev = "${includedir}/CL ${libdir}/libOpenCL${SOLIBSDEV}"
RDEPENDS:libopencl-imx= "libclc-imx"
INSANE_SKIP:libopencl-imx += "dev-so dev-deps"

FILES:libnn-imx = "${libdir}/libNN*${SOLIBS}"

INSANE_SKIP:libopenvg-imx += "dev-so"
FILES:libopenvg-imx = "${libdir}/libOpenVG*${REALSOLIBS} ${libdir}/libOpenVG*${SOLIBS}"
FILES:libopenvg-imx-dev = "${includedir}/VG ${libdir}/libOpenVG*${SOLIBSDEV}"
RDEPENDS:libopenvg-imx += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd-gpuconfig', '', d)}"

FILES:libvdk-imx = "${libdir}/libVDK*${REALSOLIBS} ${libdir}/libVDK*${SOLIBS}"
FILES:libvdk-imx-dev = "${includedir}/*vdk*.h ${libdir}/libVDK${SOLIBSDEV}"
INSANE_SKIP:libvdk-imx += "dev-so"

FILES:libvivante-dri-imx = "${libdir}/dri/vivante_dri.so"
RDEPENDS:libvivante-dri-imx = "libdrm"

INSANE_SKIP:libwayland-viv-imx += "dev-so"
FILES:libwayland-viv-imx =  "${libdir}/libwayland-viv${REALSOLIBS}  ${libdir}/libwayland-viv${SOLIBS}"
FILES:libwayland-viv-imx-dev = "${includedir}/wayland-viv ${libdir}/libwayland-viv${SOLIBSDEV} ${libdir}/pkgconfig/wayland-viv.pc"
RPROVIDES:libwayland-viv-imx += "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'xf86-video-imx-vivante', '', d)}"

INSANE_SKIP:libgc-wayland-protocol-imx += "dev-so"
FILES:libgc-wayland-protocol-imx = "${libdir}/libgc_wayland_protocol${REALSOLIBS} ${libdir}/libgc_wayland_protocol${SOLIBS}"
FILES:libgc-wayland-protocol-imx-dev = "${libdir}/libgc_wayland_protocol${SOLIBSDEV} ${libdir}/pkgconfig/gc_wayland_protocol.pc"

FILES:imx-gpu-viv-tools = "${bindir}/gmem_info"

# COMPATIBLE_MACHINE = "(mx6q-nxp-bsp|mx6dl-nxp-bsp|mx6sx-nxp-bsp|mx6sl-nxp-bsp|mx8-nxp-bsp)"
