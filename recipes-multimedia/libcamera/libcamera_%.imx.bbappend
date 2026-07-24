FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

ERROR_QA:remove = "patch-status"

# Register an imx708_wide camera sensor helper (Raspberry Pi Camera Module 3
# Wide, shipped on the NAVQ95). Upstream omits the "wide"/"noir" suffixes, so
# libcamera has no helper for the "imx708_wide" model string reported by the
# sensor.
#
# Two independent helper registries need the entry:
#  - 0001: the generic libipa CameraSensorHelper (camera_sensor_helper.cpp),
#    used by soft-ISP / generic IPA paths. Mirrors upstream commit 6a393789.
#  - 0002: the NXP neo IPA's own CameraHelper registry (cam_helper), which is
#    what the NxpNeo pipeline actually uses via CameraHelperFactoryBase::create.
#    Without this the NxpNeo IPA fails: "Failed to create camera sensor helper
#    for imx708_wide".
SRC_URI += "file://0001-libipa-camera_sensor_helper-Extend-imx708-to-imx708_wide.patch"
SRC_URI += "file://0002-ipa-nxp-cam_helper-Extend-imx708-to-imx708_wide.patch"

# Add imx708_wide to the camera sensor properties database (mirrors the imx708
# entry, same sensor) to silence the "No static properties available for
# 'imx708_wide'" warning on every camera enumeration.
SRC_URI += "file://0003-libcamera-sensor-Add-imx708_wide-sensor-properties.patch"

# libcamerasrc has no way to request an image orientation from a GStreamer
# pipeline (orientation is a configure-time property, not a runtime control).
# The RPi modules on the NAVQ95 are mounted upside-down (DT rotation = <180>),
# and the nxp/neo pipeline delivers the readout "as mounted", so plain
# libcamerasrc produces an upside-down image with no way to correct it. Add an
# "orientation" property so pipelines can do e.g.
# "libcamerasrc orientation=rot-180 ! ...".
SRC_URI += "file://0004-gstreamer-Add-orientation-property-to-libcamerasrc.patch"

# The nxp/neo IPA resolves its tuning file from "<sensor-model>.yaml"
# (neo_pipeline.cpp: ipa_->configurationFile(sensor->model() + ".yaml")),
# falling back to uncalibrated.yaml. Ship imx708_wide.yaml so the wide module
# gets its own calibration. The wide module uses the same sensor but a different
# lens, so its CCM matrixes and AF calibration differ from imx708 (data taken
# from the RPi imx708_wide.json tuning, with PDAF disabled for the neo pipeline).
SRC_URI += "file://imx708_wide.yaml"

do_install:append() {
    install -m 0644 ${UNPACKDIR}/imx708_wide.yaml \
        ${D}${datadir}/libcamera/ipa/nxp/neo/imx708_wide.yaml
}

# mali-imx moves EGL/GLES libs to ${libdir}/mali-imx/ for runtime GPU switching.
# libcamera links Qt6Gui/Qt6OpenGL which have DT_NEEDED: libEGL.so.1/libGLESv2.so.2.
# --rpath-link lets the linker resolve those transitive DT_NEEDED entries at build time.
LDFLAGS:append:imxmali = " -Wl,-rpath-link,${STAGING_LIBDIR}/mali-imx"

# Qt rcc generates *_qrc.cpp files that embed TMPDIR — known Qt build artifact
INSANE_SKIP:${PN}-src += "buildpaths"
