SUMMARY = "A prebuilt Desktop Base image as baseline for custom work"
require ubuntu-base-image.inc
SECTION = "devel"


APTGET_EXTRA_PACKAGES += " \
    libdebuginfod1 \
"
# The downside of not having the symlink destination content is that we
# are missing a few basic files that are must have for dependencies.
RPROVIDES:${PN}:ubuntu += " \
    /bin/sh \
    /bin/bash \
    /bin/dash \
"

# Extra packages that Desktop will replace and thus enables the use of 
# Yocto packages such as gstreamer to be used in Desktop without package conflitcs
# python conflitcs lib
YOCTO-DEPENDS-LIST = " \
    python2 python3  \
    python3-asyncio python3-audio \
    python3-core python3-codecs \
    python3-compression python3-crypt python3-ctypes \
    python3-datetime python3-debugger python3-doctest \
    python3-difflib python3-distutils python3-email \
    python3-html python3-io python3-json python3-logging \
    python3-math python3-mime python3-misc python3-mmap \
    python3-multiprocessing python3-netclient python3-numbers \
    python3-netserver python3-pydoc python3-pickle python3-pprint \
    python3-shell python3-stringold python3-threading python3-unittest\
    bash util-linux-blkid libreadline8 \
"
# layerscape lib conflicts
YOCTO-DEPENDS-LIST:append = " \
    e2fsprogs-badblocks e2fsprogs-tune2fs \
    e2fsprogs coreutils dtc glibc-utils procps-sysctl procps keyutils iproute2 \
    cpp pstree file gptfdisk python3-resource wpa-supplicant \
    fuser killall \
"
# imx lib conflicts
YOCTO-DEPENDS-LIST:append = " \
    adwaita-icon-theme-symbolic alsa-utils-alsactl \
    base-files bzip2-dev ca-certificates dbus desktop-file-utils \
    e2fsprogs-e2fsck gsettings-desktop-schemas kmod \
    libpam-runtime libgtk-3.0 gtk+3 \
    libx11-locale libglib-2.0-0 \
    shadow shadow-base update-alternatives-opkg \
    openssl openssl-conf openssl-dev elfutils \
    perl udev udev-hwdb upower util-linux-lsblk xrandr xkbcomp \
    systemd systemd-udev-rules libglib-2.0-utils ldconfig systemd-extra-utils \
    alsa-base libao-common \
"
#    libgudev-1.0-0 libgudev-1.0-dbg libgudev-1.0-dev \
# others 
YOCTO-DEPENDS-LIST:append = " \
alsa-topology-conf attr attr-dev avahi-daemon avahi-dev  \
base-files-dev base-passwd base-passwd-dev bash-completion bash-completion-dev  \
bash-dev bluez5 bluez5-dev \
consolekit consolekit-dev cracklib cracklib-dev  \
cryptodev-linux cryptodev-linux-dev curl curl-devflac-dev  \
encodings-dev fio flex-dev flex-libfl font-util font-util-dev  \
glib-2.0 glib-networking gobject-introspection  \
gobject-introspection-dev gsettings-desktop-schemas-dev gtk+3-dev gtk+-dev \
hicolor-icon-theme i2c-tools ibxshmfence-dev icu icu-dev  \
ifenslave init-system-helpers-service iproute2-ip iso-codes-dev  \
lame lame-dev libasm1 libasound2 libatk-1.0-0 libatk-bridge-2.0-0  \
libatomic-dev libatomic-ops libatomic-ops-dev libatopology2  \
libatspi0 libattr1 libavahi-client3 libavahi-common3  \
libavahi-core7 libavahi-glib1 libavahi-gobject0  \
libblkid1 libc6-dbg libcairo-dbg  \
libcairo-dev  \
libcanberra-dev libcanberra-gtk2  \
libcap libcap2 libcap-dbg libcap-dev libcap-ng0 libcap-ng-dev  \
libcogl-gles2-20 \
libcrypt libcrypt-dbg libcrypt-dev libcrypto libcrypto1.1 libcrypto3  \
libdaemon0 libdaemon-dev libdbus-1-3 libdbus-glib-1-2 libdbus-glib-1-dev  \
libdebuginfod1 libdrm-amdgpu libdrm-dev libdrm-etnaviv  \
libdrm-exynos libdrm-freedreno libdrm-intel libdrm-nouveau  \
libdrm-omap libdrm-radeon libdw1 libelf1  \
libexpat1 libexpat-dbg libexpat-dev libfdisk1  \
libffi7 libffi-dbg libffi-dev  \
libfontconfig-dbg libfontconfig-dev  \
libfreetype-dbg libfreetype-dev  \
libfribidi-dbg libfribidi-dev  \
libgcc libgcc1 libgcc-s-dbg  \
libgdk-pixbuf-2.0-loader-gif libgdk-pixbuf-2.0-loader-jpeg  \
libgdk-pixbuf-2.0-loader-png libgdk-pixbuf-2.0-loader-xpm libgdk-pixbuf-xlib-2.0-0  \
libgtk-2.0 libglib-2.0-dbg libglib-2.0-dev \
libgmp10 libgmp-dbg libgmp-dev libgmpxx4  \
libgnutls30 libgnutls-dev libgnutls-openssl27 libgnutlsxx28  \
libharfbuzz-dbg libharfbuzz-dev  \
libical libical-dev  \
libice6 libice-dev  \
libicudata64 libicudata67 libicui18n64 libicui18n67  \
libicuio64 libicutu64 libicuuc64 libicuuc67  \
libidn2-0 libidn2-dbg libidn2-dev  \
libinput10 libjpeg-dbg libjpeg-dev  \
libkmod2 liblzma5  \
libmp3lame-dev libnss-mdns  \
libogg-dbg libogg-dev  \
libpam libpam-dev libpci3  \
libpciaccess0 libpciaccess-dbg libpciaccess-dev  \
libpcrecpp0 libpcre-dbg libpcre-dev libpcreposix0  \
libpsl5 libpsl-dbg libpsl-dev  \
libpthread-stubs-dev libpulse-mainloop-glib0 libpulse-simple0  \
libreadline-dev  \
libsbc-dbg libsbc-dev  \
libsm6 libsmartcols1 libsm-dev  \
libsndfile-dbg libsndfile-dev  \
libsoup-2.4 libsoup-2.4-dbg libsoup-2.4-dev  \
libspeex-dbg libspeex-dev libspeexdsp1 libspeexdsp-dev  \
libsqlite3-0 libsqlite3-dev  \
libssl libssl1.1 libssl3  \
libstdc++6 libstdc++-dev libsysfs2  libsystemd0  \
libtag-c0 libtag-dbg libtag-dev  \
libticw5 libtool libtool-dev libturbojpeg0 libudev1  \
libunistring2 libunistring-dbg libunistring-dev  \
libunwind \
libuuid1 \
libx11-6 libx11-dbg libx11-dev libx11-xcb1  \
libxau6 libxau-dbg libxau-dev  \
libxcb1 libxcb-composite0 libxcb-damage0 libxcb-dbg  \
libxcb-dev libxcb-dpms0 libxcb-dri2-0 libxcb-dri3-0  \
libxcb-glx0 libxcb-present0 libxcb-randr0 libxcb-record0  \
libxcb-res0 libxcb-screensaver0  \
libxcb-shape0 libxcb-shm0 libxcb-sync1 libxcb-util1  \
libxcb-util-dev libxcb-xf86dri0 libxcb-xfixes0 libxcb-xinerama0  \
libxcb-xinput0 libxcb-xkb1 libxcb-xtest0 libxcb-xv0 libxcb-xvmc0  \
libxcomposite-dev libxcursor-dev  \
libxdmcp6 libxdmcp-dbg libxdmcp-dev  \
libxext6 libxext-dbg libxext-dev libxfixes3 libxfixes-dbg libxfixes-dev  \
libxft-dbg libxft-dev  \
libxi6 libxi-dev  \
libxkbfile1 libxkbfile-dev libxml2 libxml2-dbg libxml2-dev  \
libxrandr-dev libxrender-dbg libxrender-dev  \
libxshmfence1 libxshmfence-dev libxtst-dev  \
libxv-dbg libxv-dev libxxf86vm1 libxxf86vm-dev  \
libz1 libz-dbg libz-dev libzstd1 linux-libc-headers-dev  \
lrzsz memtester merge-files mesa-dev minicom  \
mkfontdir mkfontdir-dev mkfontscale mkfontscale-dev  \
mmc-utils mozjs mozjs-dev  \
ncurses-dev nettle-dbg nettle-dev \
orc orc-dev  \
pciutils perl-dev pkgconfig polkit polkit-dev  \
psmisc ptpd pulseaudio pulseaudio-dev  \
python2 python3-compile python3-dbus python3-dbus-dev  \
python3-dev python3-nose python3-numpy python3-pkgutil  \
python3-plistlib python3-pycairo python3-pycairo-dev  \
python3-pyelftools python3-pygobject python3-pygobject-dev  \
python3-typing python3-unixadmin python3-xml python3-xmlrpc  \
rgb shadow-dev shared-mime-info-dev slang sysfsutils sysklogd  \
sysstat update-rc.d usbutils util-linux-dev util-macros-dev  \
valgrind valgrind-dev wireless-tools  \
xcb-proto-dev xf86-input-libinput xkeyboard-config xkeyboard-config-dev  \
xorgproto-dev xrandr-dev xserver-xf86-config xtrans-dev zstd \
xz \
"

RCONFLICTS:${PN}-base = " ${YOCTO-DEPENDS-LIST} "
RCONFLICTS:${PN}-ubuntu-base = " ${YOCTO-DEPENDS-LIST} "
RCONFLICTS:${PN} = " ${YOCTO-DEPENDS-LIST} "
RREPLACES:${PN} = " ${YOCTO-DEPENDS-LIST} "
RREPLACES:${PN}-base = " ${YOCTO-DEPENDS-LIST} "
RREPLACES:${PN}-ubuntu-base = " ${YOCTO-DEPENDS-LIST} "
RPROVIDES:${PN} = " ${YOCTO-DEPENDS-LIST} "
RPROVIDES:${PN}-base = " ${YOCTO-DEPENDS-LIST} "
RPROVIDES:${PN}-ubuntu-base = " ${YOCTO-DEPENDS-LIST} "

RPROVIDES:${PN}:ubuntu += " libglib-2.0 "

do_install[network] = "1"

QA_EMPTY_DIRS = ""

python do_package:append() {
    os.environ['PSEUDO_DISABLED'] = '1'
    d.setVar('ERROR_QA', '')
}

APTGET_EXTRA_PACKAGES += " \
    udhcpc \
    freeglut3-dev \
    libglu1-mesa \
    python3 \
    libegl-dev \
    libgl-dev \
    libgles-dev \
"