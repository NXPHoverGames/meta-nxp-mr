# A desktop image with an Desktop rootfs
#
# Note that we have a tight dependency to ubuntu-base
# and that we cannot just install arbitrary Yocto packages to avoid
# rootfs pollution or destruction.
PV = "${@d.getVar('PREFERRED_VERSION_ubuntu-base', True) or '1.0'}"

require fsl-image-common.inc

ROOTFS_POSTPROCESS_COMMAND:append = "do_save_cheese;"

ML_NNSTREAMER_PKGS = " \
    nnstreamer \
    nnstreamer-tensorflow-lite \
    nnstreamer-python3 \
    nnstreamer-protobuf \
"

# This must be added first as it provides the foundation for
# subsequent modifications to the rootfs
IMAGE_INSTALL += "\
	ubuntu-base \
	ubuntu-base-dev \
	ubuntu-base-dbg \
	ubuntu-base-doc \
"

IMAGE_INSTALL += "\
	firmwared \
    udev-extraconf \
    packagegroup-fsl-gstreamer1.0 \
    gstreamer1.0 \
	gstreamer1.0-plugins-good-adaptivedemux2 \
	gstreamer1.0-plugins-good-alaw \
	gstreamer1.0-plugins-good-alpha \
	gstreamer1.0-plugins-good-alphacolor \
	gstreamer1.0-plugins-good-apetag \
	gstreamer1.0-plugins-good-auparse \
	gstreamer1.0-plugins-good-autodetect \
	gstreamer1.0-plugins-good-avi \
	gstreamer1.0-plugins-good-cutter \
	gstreamer1.0-plugins-good-deinterlace \
	gstreamer1.0-plugins-good-flxdec \
	gstreamer1.0-plugins-good-gdkpixbuf \
	gstreamer1.0-plugins-good-icydemux \
	gstreamer1.0-plugins-good-id3demux \
	gstreamer1.0-plugins-good-imagefreeze \
	gstreamer1.0-plugins-good-interleave \
	gstreamer1.0-plugins-good-isomp4 \
	gstreamer1.0-plugins-good-jpeg \
	gstreamer1.0-plugins-good-level \
	gstreamer1.0-plugins-good-matroska \
	gstreamer1.0-plugins-good-monoscope \
	gstreamer1.0-plugins-good-mpg123 \
	gstreamer1.0-plugins-good-mulaw \
	gstreamer1.0-plugins-good-multifile \
	gstreamer1.0-plugins-good-multipart \
	gstreamer1.0-plugins-good-navigationtest \
	gstreamer1.0-plugins-good-png \
	gstreamer1.0-plugins-good-replaygain \
	gstreamer1.0-plugins-good-rtp \
	gstreamer1.0-plugins-good-rtpmanager \
	gstreamer1.0-plugins-good-rtsp \
	gstreamer1.0-plugins-good-shapewipe \
	gstreamer1.0-plugins-good-smpte \
	gstreamer1.0-plugins-good-soup \
	gstreamer1.0-plugins-good-spectrum \
	gstreamer1.0-plugins-good-speex \
	gstreamer1.0-plugins-good-taglib \
	gstreamer1.0-plugins-good-udp \
	gstreamer1.0-plugins-good-video4linux2 \
	gstreamer1.0-plugins-good-videobox \
	gstreamer1.0-plugins-good-videocrop \
	gstreamer1.0-plugins-good-videofilter \
	gstreamer1.0-plugins-good-videomixer \
	gstreamer1.0-plugins-good-wavenc \
	gstreamer1.0-plugins-good-wavparse \
	gstreamer1.0-plugins-good-xingmux \
	gstreamer1.0-plugins-good-y4menc \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
	gstreamer1.0-plugins-bad-accurip \
	gstreamer1.0-plugins-bad-adpcmdec \
	gstreamer1.0-plugins-bad-adpcmenc \
	gstreamer1.0-plugins-bad-aes \
	gstreamer1.0-plugins-bad-aiff \
	gstreamer1.0-plugins-bad-apps \
	gstreamer1.0-plugins-bad-asfmux \
	gstreamer1.0-plugins-bad-audiobuffersplit \
	gstreamer1.0-plugins-bad-audiofxbad \
	gstreamer1.0-plugins-bad-audiolatency \
	gstreamer1.0-plugins-bad-audiomixmatrix \
	gstreamer1.0-plugins-bad-audiovisualizers \
	gstreamer1.0-plugins-bad-autoconvert \
	gstreamer1.0-plugins-bad-autoselect \
	gstreamer1.0-plugins-bad-bayer \
	gstreamer1.0-plugins-bad-bluez \
	gstreamer1.0-plugins-bad-bz2 \
	gstreamer1.0-plugins-bad-camerabin \
	gstreamer1.0-plugins-bad-codecalpha \
	gstreamer1.0-plugins-bad-codectimestamper \
	gstreamer1.0-plugins-bad-coloreffects \
	gstreamer1.0-plugins-bad-dash \
	gstreamer1.0-plugins-bad-debugutilsbad \
	gstreamer1.0-plugins-bad-decklink \
	gstreamer1.0-plugins-bad-dvb \
	gstreamer1.0-plugins-bad-dvbsubenc \
	gstreamer1.0-plugins-bad-dvbsuboverlay \
	gstreamer1.0-plugins-bad-dvdspu \
	gstreamer1.0-plugins-bad-faceoverlay \
	gstreamer1.0-plugins-bad-fbdevsink \
	gstreamer1.0-plugins-bad-festival \
	gstreamer1.0-plugins-bad-fieldanalysis \
	gstreamer1.0-plugins-bad-freeverb \
	gstreamer1.0-plugins-bad-frei0r \
	gstreamer1.0-plugins-bad-gaudieffects \
	gstreamer1.0-plugins-bad-gdp \
	gstreamer1.0-plugins-bad-geometrictransform \
	gstreamer1.0-plugins-bad-hls \
	gstreamer1.0-plugins-bad-id3tag \
	gstreamer1.0-plugins-bad-insertbin \
	gstreamer1.0-plugins-bad-inter \
	gstreamer1.0-plugins-bad-interlace \
	gstreamer1.0-plugins-bad-ipcpipeline \
	gstreamer1.0-plugins-bad-ivfparse \
	gstreamer1.0-plugins-bad-ivtc \
	gstreamer1.0-plugins-bad-jp2kdecimator \
	gstreamer1.0-plugins-bad-jpegformat \
	gstreamer1.0-plugins-bad-legacyrawparse \
	gstreamer1.0-plugins-bad-midi \
	gstreamer1.0-plugins-bad-mpegpsdemux \
	gstreamer1.0-plugins-bad-mpegpsmux \
	gstreamer1.0-plugins-bad-mpegtsdemux \
	gstreamer1.0-plugins-bad-mpegtsmux \
	gstreamer1.0-plugins-bad-mse \
	gstreamer1.0-plugins-bad-mxf \
	gstreamer1.0-plugins-bad-netsim \
	gstreamer1.0-plugins-bad-pcapparse \
	gstreamer1.0-plugins-bad-pnm \
	gstreamer1.0-plugins-bad-proxy \
	gstreamer1.0-plugins-bad-removesilence \
	gstreamer1.0-plugins-bad-rfbsrc \
	gstreamer1.0-plugins-bad-rist \
	gstreamer1.0-plugins-bad-rtmp2 \
	gstreamer1.0-plugins-bad-rtpmanagerbad \
	gstreamer1.0-plugins-bad-rtponvif \
	gstreamer1.0-plugins-bad-sdpelem \
	gstreamer1.0-plugins-bad-segmentclip \
	gstreamer1.0-plugins-bad-shm \
	gstreamer1.0-plugins-bad-siren \
	gstreamer1.0-plugins-bad-smooth \
	gstreamer1.0-plugins-bad-smoothstreaming \
	gstreamer1.0-plugins-bad-speed \
	gstreamer1.0-plugins-bad-src \
	gstreamer1.0-plugins-bad-subenc \
	gstreamer1.0-plugins-bad-switchbin \
	gstreamer1.0-plugins-bad-timecode \
	gstreamer1.0-plugins-bad-tinycompress \
	gstreamer1.0-plugins-bad-transcode \
	gstreamer1.0-plugins-bad-unixfd \
	gstreamer1.0-plugins-bad-uvcgadget \
	gstreamer1.0-plugins-bad-uvch264 \
	gstreamer1.0-plugins-bad-videofiltersbad \
	gstreamer1.0-plugins-bad-videoframe-audiolevel \
	gstreamer1.0-plugins-bad-videoparsersbad \
	gstreamer1.0-plugins-bad-videosignal \
	gstreamer1.0-plugins-bad-vmnc \
	gstreamer1.0-plugins-bad-y4mdec \
    gstreamer1.0-plugins-base \
	gstreamer1.0-plugins-base-adder \
	gstreamer1.0-plugins-base-app \
	gstreamer1.0-plugins-base-apps \
	gstreamer1.0-plugins-base-basedebug \
	gstreamer1.0-plugins-base-compositor \
	gstreamer1.0-plugins-base-dsd \
	gstreamer1.0-plugins-base-encoding \
	gstreamer1.0-plugins-base-overlaycomposition \
	gstreamer1.0-plugins-base-pbtypes \
	gstreamer1.0-plugins-base-playback \
	gstreamer1.0-plugins-base-rawparse \
	gstreamer1.0-plugins-base-src \
	gstreamer1.0-plugins-base-subparse \
	gstreamer1.0-plugins-base-tcp \
	gstreamer1.0-plugins-base-theora \
	gstreamer1.0-plugins-base-typefindfunctions \
	gstreamer1.0-plugins-base-videoconvertscale \
	gstreamer1.0-plugins-base-videorate \
	gstreamer1.0-plugins-base-videotestsrc \
	gstreamer1.0-plugins-base-ximagesink \
	gstreamer1.0-plugins-base-xvimagesink \
	tensorflow-lite \
	tensorflow-lite-vx-delegate \
"

# gstreamer1.0-plugins-good-ximagesrc libxtst6
# gstreamer1.0-plugins-good-gtk libxrender1
# gstreamer1.0-plugins-good-cairo
# gstreamer1.0-plugins-good-pulseaudio

APTGET_EXTRA_PACKAGES += "\
	ntpdate patchelf \
"

##############################################################################
# NOTE: We cannot install arbitrary Yocto packages as they will
# conflict with the content of the prebuilt Desktop rootfs and pull
# in dependencies that may break the rootfs.
# Any package addition needs to be carefully evaluated with respect
# to the final image that we build.
##############################################################################

# GPU driver
G2D_SAMPLES                 = ""
G2D_SAMPLES:imxgpu2d        = "imx-g2d-samples"
G2D_SAMPLES:imxdpu          = "imx-g2d-samples"

IMAGE_INSTALL:remove:imx95-19x19-lpddr5-evk = " \
    libgles3-imx-dev \
    libopencl-imx \
    libvulkan-imx \
	libopencl-imx \
	libgal-imx \
    packagegroup-fsl-gstreamer1.0 \
    gstreamer1.0 \
	gstreamer1.0-plugins-good-adaptivedemux2 \
	gstreamer1.0-plugins-good-alaw \
	gstreamer1.0-plugins-good-alpha \
	gstreamer1.0-plugins-good-alphacolor \
	gstreamer1.0-plugins-good-apetag \
	gstreamer1.0-plugins-good-auparse \
	gstreamer1.0-plugins-good-autodetect \
	gstreamer1.0-plugins-good-avi \
	gstreamer1.0-plugins-good-cutter \
	gstreamer1.0-plugins-good-deinterlace \
	gstreamer1.0-plugins-good-flxdec \
	gstreamer1.0-plugins-good-gdkpixbuf \
	gstreamer1.0-plugins-good-icydemux \
	gstreamer1.0-plugins-good-id3demux \
	gstreamer1.0-plugins-good-imagefreeze \
	gstreamer1.0-plugins-good-interleave \
	gstreamer1.0-plugins-good-isomp4 \
	gstreamer1.0-plugins-good-jpeg \
	gstreamer1.0-plugins-good-level \
	gstreamer1.0-plugins-good-matroska \
	gstreamer1.0-plugins-good-monoscope \
	gstreamer1.0-plugins-good-mpg123 \
	gstreamer1.0-plugins-good-mulaw \
	gstreamer1.0-plugins-good-multifile \
	gstreamer1.0-plugins-good-multipart \
	gstreamer1.0-plugins-good-navigationtest \
	gstreamer1.0-plugins-good-png \
	gstreamer1.0-plugins-good-replaygain \
	gstreamer1.0-plugins-good-rtp \
	gstreamer1.0-plugins-good-rtpmanager \
	gstreamer1.0-plugins-good-rtsp \
	gstreamer1.0-plugins-good-shapewipe \
	gstreamer1.0-plugins-good-smpte \
	gstreamer1.0-plugins-good-soup \
	gstreamer1.0-plugins-good-spectrum \
	gstreamer1.0-plugins-good-speex \
	gstreamer1.0-plugins-good-taglib \
	gstreamer1.0-plugins-good-udp \
	gstreamer1.0-plugins-good-video4linux2 \
	gstreamer1.0-plugins-good-videobox \
	gstreamer1.0-plugins-good-videocrop \
	gstreamer1.0-plugins-good-videofilter \
	gstreamer1.0-plugins-good-videomixer \
	gstreamer1.0-plugins-good-wavenc \
	gstreamer1.0-plugins-good-wavparse \
	gstreamer1.0-plugins-good-xingmux \
	gstreamer1.0-plugins-good-y4menc \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
	gstreamer1.0-plugins-bad-accurip \
	gstreamer1.0-plugins-bad-adpcmdec \
	gstreamer1.0-plugins-bad-adpcmenc \
	gstreamer1.0-plugins-bad-aes \
	gstreamer1.0-plugins-bad-aiff \
	gstreamer1.0-plugins-bad-apps \
	gstreamer1.0-plugins-bad-asfmux \
	gstreamer1.0-plugins-bad-audiobuffersplit \
	gstreamer1.0-plugins-bad-audiofxbad \
	gstreamer1.0-plugins-bad-audiolatency \
	gstreamer1.0-plugins-bad-audiomixmatrix \
	gstreamer1.0-plugins-bad-audiovisualizers \
	gstreamer1.0-plugins-bad-autoconvert \
	gstreamer1.0-plugins-bad-autoselect \
	gstreamer1.0-plugins-bad-bayer \
	gstreamer1.0-plugins-bad-bluez \
	gstreamer1.0-plugins-bad-bz2 \
	gstreamer1.0-plugins-bad-camerabin \
	gstreamer1.0-plugins-bad-codecalpha \
	gstreamer1.0-plugins-bad-codectimestamper \
	gstreamer1.0-plugins-bad-coloreffects \
	gstreamer1.0-plugins-bad-dash \
	gstreamer1.0-plugins-bad-debugutilsbad \
	gstreamer1.0-plugins-bad-decklink \
	gstreamer1.0-plugins-bad-dvb \
	gstreamer1.0-plugins-bad-dvbsubenc \
	gstreamer1.0-plugins-bad-dvbsuboverlay \
	gstreamer1.0-plugins-bad-dvdspu \
	gstreamer1.0-plugins-bad-faceoverlay \
	gstreamer1.0-plugins-bad-fbdevsink \
	gstreamer1.0-plugins-bad-festival \
	gstreamer1.0-plugins-bad-fieldanalysis \
	gstreamer1.0-plugins-bad-freeverb \
	gstreamer1.0-plugins-bad-frei0r \
	gstreamer1.0-plugins-bad-gaudieffects \
	gstreamer1.0-plugins-bad-gdp \
	gstreamer1.0-plugins-bad-geometrictransform \
	gstreamer1.0-plugins-bad-hls \
	gstreamer1.0-plugins-bad-id3tag \
	gstreamer1.0-plugins-bad-insertbin \
	gstreamer1.0-plugins-bad-inter \
	gstreamer1.0-plugins-bad-interlace \
	gstreamer1.0-plugins-bad-ipcpipeline \
	gstreamer1.0-plugins-bad-ivfparse \
	gstreamer1.0-plugins-bad-ivtc \
	gstreamer1.0-plugins-bad-jp2kdecimator \
	gstreamer1.0-plugins-bad-jpegformat \
	gstreamer1.0-plugins-bad-legacyrawparse \
	gstreamer1.0-plugins-bad-midi \
	gstreamer1.0-plugins-bad-mpegpsdemux \
	gstreamer1.0-plugins-bad-mpegpsmux \
	gstreamer1.0-plugins-bad-mpegtsdemux \
	gstreamer1.0-plugins-bad-mpegtsmux \
	gstreamer1.0-plugins-bad-mse \
	gstreamer1.0-plugins-bad-mxf \
	gstreamer1.0-plugins-bad-netsim \
	gstreamer1.0-plugins-bad-pcapparse \
	gstreamer1.0-plugins-bad-pnm \
	gstreamer1.0-plugins-bad-proxy \
	gstreamer1.0-plugins-bad-removesilence \
	gstreamer1.0-plugins-bad-rfbsrc \
	gstreamer1.0-plugins-bad-rist \
	gstreamer1.0-plugins-bad-rtmp2 \
	gstreamer1.0-plugins-bad-rtpmanagerbad \
	gstreamer1.0-plugins-bad-rtponvif \
	gstreamer1.0-plugins-bad-sdpelem \
	gstreamer1.0-plugins-bad-segmentclip \
	gstreamer1.0-plugins-bad-shm \
	gstreamer1.0-plugins-bad-siren \
	gstreamer1.0-plugins-bad-smooth \
	gstreamer1.0-plugins-bad-smoothstreaming \
	gstreamer1.0-plugins-bad-speed \
	gstreamer1.0-plugins-bad-src \
	gstreamer1.0-plugins-bad-subenc \
	gstreamer1.0-plugins-bad-switchbin \
	gstreamer1.0-plugins-bad-timecode \
	gstreamer1.0-plugins-bad-tinycompress \
	gstreamer1.0-plugins-bad-transcode \
	gstreamer1.0-plugins-bad-unixfd \
	gstreamer1.0-plugins-bad-uvcgadget \
	gstreamer1.0-plugins-bad-uvch264 \
	gstreamer1.0-plugins-bad-videofiltersbad \
	gstreamer1.0-plugins-bad-videoframe-audiolevel \
	gstreamer1.0-plugins-bad-videoparsersbad \
	gstreamer1.0-plugins-bad-videosignal \
	gstreamer1.0-plugins-bad-vmnc \
	gstreamer1.0-plugins-bad-y4mdec \
    gstreamer1.0-plugins-base \
	gstreamer1.0-plugins-base-adder \
	gstreamer1.0-plugins-base-app \
	gstreamer1.0-plugins-base-apps \
	gstreamer1.0-plugins-base-basedebug \
	gstreamer1.0-plugins-base-compositor \
	gstreamer1.0-plugins-base-dsd \
	gstreamer1.0-plugins-base-encoding \
	gstreamer1.0-plugins-base-overlaycomposition \
	gstreamer1.0-plugins-base-pbtypes \
	gstreamer1.0-plugins-base-playback \
	gstreamer1.0-plugins-base-rawparse \
	gstreamer1.0-plugins-base-src \
	gstreamer1.0-plugins-base-subparse \
	gstreamer1.0-plugins-base-tcp \
	gstreamer1.0-plugins-base-theora \
	gstreamer1.0-plugins-base-typefindfunctions \
	gstreamer1.0-plugins-base-videoconvertscale \
	gstreamer1.0-plugins-base-videorate \
	gstreamer1.0-plugins-base-videotestsrc \
	gstreamer1.0-plugins-base-ximagesink \
	gstreamer1.0-plugins-base-xvimagesink \
    libopenvx-imx libopenvx-imx-dev \
    libnn-imx \
    tensorflow-lite \
    tensorflow-lite-vx-delegate \
    ${ML_NNSTREAMER_PKGS} \
"

fakeroot do_save_cheese() {
	set -x

	if [ -e "${IMAGE_ROOTFS}/usr/bin/cheese" ]; then
		# backup cheese bin
		mv ${IMAGE_ROOTFS}/usr/bin/cheese ${IMAGE_ROOTFS}/usr/bin/cheese_imx
	fi

	set +x
}
