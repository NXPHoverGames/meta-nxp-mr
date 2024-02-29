SUMMARY = "A prebuilt Desktop Base image as baseline for custom work"
require ubuntu-license.inc
SECTION = "devel"

# Desktop 23.10 baseline
SRC_URI[md5sum] = "2605f0a56046ada75cdff7e25b86610b"
SRC_URI[sha256sum] = "2c5cc32b7523e3632d3fe3e5fe08d18220a4dfb73aea2c4fcab66cf3851e062f"

require ubuntu-base.inc

# There are some basic differences between different Desktop versions.
# We try not to address them in the generic recipe
APTGET_EXTRA_PACKAGES += ""

# Desktop 20 unifies things and turns some things into symlinks. We
# solve this with Yocto "usrmerge" but that isn't quite enough.
# We still need to ship the symlinks.
# We also need to remove the udev reference as apparently bitbake.conf
# isn't quite adapted to usrmerge there.
FILES:${PN}:remove = "/lib/udev"
FILES:${PN} += "/bin"
FILES:${PN} += "/sbin"

# The downside of not having the symlink destination content is that we
# are missing a few basic files that are must have for dependencies.
RPROVIDES:${PN}:ubuntu += " \
    /bin/sh \
    /bin/bash \
    /bin/dash \
"

APTGET_EXTRA_PACKAGES += " \
    libdebuginfod1 \
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
    adwaita-icon-theme-symbolic alsa-ucm-conf alsa-utils-alsactl at-spi2-core \
    base-files bzip2-dev ca-certificates dbus desktop-file-utils \
    e2fsprogs-e2fsck gsettings-desktop-schemas kmod \
    libfontconfig1 libpam-runtime libgtk-3.0 libgtk-4.0 gtk+3 \
    libxcvt libpulse0 libx11-locale libglib-2.0-utils libgudev-1.0-0 libglib-2.0-0 \
    shadow shadow-base update-alternatives-opkg \
    systemd \
    openssl openssl-conf openssl-dev elfutils \
    perl udev udev-hwdb upower util-linux-lsblk xrandr xkbcomp \
"
# others 
YOCTO-DEPENDS-LIST:append = " \
alsa-topology-conf attr attr-dev avahi-daemon avahi-dev  \
base-files-dev base-passwd base-passwd-dev bash-completion bash-completion-dev  \
bash-dev bluez5 bluez5-dev \
consolekit consolekit-dev cracklib cracklib-dev  \
cryptodev-linux cryptodev-linux-dev curl curl-devflac-dev  \
encodings-dev fio flex-dev flex-libfl font-util font-util-dev  \
gdk-pixbuf glib-2.0 glib-networking gobject-introspection  \
gobject-introspection-dev gsettings-desktop-schemas-dev gtk+3-dev gtk+-dev \
hicolor-icon-theme i2c-tools ibxshmfence-dev icu icu-dev  \
ifenslave init-system-helpers-service iproute2-ip iso-codes-dev  \
lame lame-dev libasm1 libasound2 libatk-1.0-0 libatk-bridge-2.0-0  \
libatomic-dev libatomic-ops libatomic-ops-dev libatopology2  \
libatspi0 libattr1 libavahi-client3 libavahi-common3  \
libavahi-core7 libavahi-glib1 libavahi-gobject0  \
libblkid1 libc6-dbg libcairo2 libcairo-dbg  \
libcairo-dev libcairo-gobject2 libcairo-script-interpreter2  \
libcanberra libcanberra-dev libcanberra-gtk2 libcanberra-gtk3  \
libcap libcap2 libcap-dbg libcap-dev libcap-ng0 libcap-ng-dev  \
libcogl-gles2-20 \
libcrypt libcrypt-dbg libcrypt-dev libcrypto libcrypto1.1 libcrypto3  \
libdaemon0 libdaemon-dev libdbus-1-3 libdbus-glib-1-2 libdbus-glib-1-dev  \
libdebuginfod1 libdrm-amdgpu libdrm-dev libdrm-etnaviv  \
libdrm-exynos libdrm-freedreno libdrm-intel libdrm-nouveau  \
libdrm-omap libdrm-radeon libdw1 libelf1 libepoxy0 libepoxy-dev  \
libexpat1 libexpat-dbg libexpat-dev libfdisk1  \
libffi7 libffi-dbg libffi-dev  \
libfontconfig-dbg libfontconfig-dev  \
libfontenc1 libfontenc-dev  \
libfreetype6 libfreetype-dbg libfreetype-dev  \
libfribidi0 libfribidi-dbg libfribidi-dev  \
libgcc libgcc1 libgcc-s-dbg  \
libgdk-pixbuf-2.0-0 libgdk-pixbuf-2.0-dbg libgdk-pixbuf-2.0-dev  \
libgdk-pixbuf-2.0-loader-gif libgdk-pixbuf-2.0-loader-jpeg  \
libgdk-pixbuf-2.0-loader-png libgdk-pixbuf-2.0-loader-xpm libgdk-pixbuf-xlib-2.0-0  \
libgtk-2.0 libglib-2.0-dbg libglib-2.0-dev \
libgmp10 libgmp-dbg libgmp-dev libgmpxx4  \
libgnutls30 libgnutls-dev libgnutls-openssl27 libgnutlsxx28  \
libgudev-1.0-dbg libgudev-1.0-dev  \
libharfbuzz0 libharfbuzz-dbg libharfbuzz-dev  \
libical libical-dev  \
libice6 libice-dev  \
libicudata64 libicudata67 libicui18n64 libicui18n67  \
libicuio64 libicutu64 libicuuc64 libicuuc67  \
libidn2-0 libidn2-dbg libidn2-dev  \
libinput10 libjpeg-dbg libjpeg-dev  \
libjson-glib-1.0-0 libjson-glib-1.0-dev  \
libkmod2 libltdl7 liblzma5 libmount1  \
libmp3lame0 libmp3lame-dev libnss-mdns  \
libogg0 libogg-dbg libogg-dev liborc-0.4-0 liborc-test-0.4-0  \
libpam libpam-dev libpci3  \
libpciaccess0 libpciaccess-dbg libpciaccess-dev  \
libpcrecpp0 libpcre-dbg libpcre-dev libpcreposix0  \
libpixman-1-0 libpixman-1-dbg libpixman-1-dev  \
libpng16-16 libpng16-dbg libpng16-dev  \
libpsl5 libpsl-dbg libpsl-dev  \
libpthread-stubs-dev libpulse-mainloop-glib0 libpulse-simple0  \
libreadline-dev librsvg-2-2 librsvg-2-dbg librsvg-2-dev  \
libsbc1 libsbc-dbg libsbc-dev  \
libsm6 libsmartcols1 libsm-dev  \
libsndfile1 libsndfile-dbg libsndfile-dev  \
libsoup-2.4 libsoup-2.4-dbg libsoup-2.4-dev  \
libspeex1 libspeex-dbg libspeex-dev libspeexdsp1 libspeexdsp-dev  \
libsqlite3-0 libsqlite3-dev  \
libssl libssl1.1 libssl3  \
libstdc++6 libstdc++-dev libsysfs2  libsystemd0  \
libtag1 libtag-c0 libtag-dbg libtag-dev  \
libtheora libtheora-dbg libtheora-dev  \
libticw5 libtool libtool-dev libturbojpeg0 libudev1  \
libunistring2 libunistring-dbg libunistring-dev  \
libunwind libusb-1.0-0 libusb-1.0-dbg libusb-1.0-dev  \
libuuid1 libvorbis libvorbis-dbg libvorbis-dev  \
libwebp libx11-6 libx11-dbg libx11-dev libx11-xcb1  \
libxau6 libxau-dbg libxau-dev  \
libxcb1 libxcb-composite0 libxcb-damage0 libxcb-dbg  \
libxcb-dev libxcb-dpms0 libxcb-dri2-0 libxcb-dri3-0  \
libxcb-glx0 libxcb-present0 libxcb-randr0 libxcb-record0  \
libxcb-render0 libxcb-res0 libxcb-screensaver0  \
libxcb-shape0 libxcb-shm0 libxcb-sync1 libxcb-util1  \
libxcb-util-dev libxcb-xf86dri0 libxcb-xfixes0 libxcb-xinerama0  \
libxcb-xinput0 libxcb-xkb1 libxcb-xtest0 libxcb-xv0 libxcb-xvmc0  \
libxcomposite1 libxcomposite-dev libxcursor1 libxcursor-dev  \
libxdamage1 libxdamage-dbg libxdamage-dev  \
libxdmcp6 libxdmcp-dbg libxdmcp-dev  \
libxext6 libxext-dbg libxext-dev libxfixes3 libxfixes-dbg libxfixes-dev  \
libxfont2-2 libxfont2-dev libxft2 libxft-dbg libxft-dev  \
libxi6 libxi-dev libxkbcommon libxkbcommon0 libxkbcommon-dev  \
libxkbfile1 libxkbfile-dev libxml2 libxml2-dbg libxml2-dev  \
libxrandr2 libxrandr-dev libxrender1 libxrender-dbg libxrender-dev  \
libxshmfence1 libxshmfence-dev libxslt libxtst6 libxtst-dev  \
libxv1 libxv-dbg libxv-dev libxxf86vm1 libxxf86vm-dev  \
libz1 libz-dbg libz-dev libzstd1 linux-libc-headers-dev  \
lrzsz memtester merge-files mesa-dev minicom  \
mkfontdir mkfontdir-dev mkfontscale mkfontscale-dev  \
mmc-utils mozjs mozjs-dev mpg123 mpg123-dbg mpg123-dev  \
ncurses-dev nettle-dbg nettle-dev nspr nspr-dev  \
nss orc orc-dev pango pango-dbg pango-dev  \
pciutils perl-dev pkgconfig polkit polkit-dev  \
psmisc ptpd pulseaudio pulseaudio-dev  \
python2 python3-compile python3-dbus python3-dbus-dev  \
python3-dev python3-nose python3-numpy python3-pkgutil  \
python3-plistlib python3-pycairo python3-pycairo-dev  \
python3-pyelftools python3-pygobject python3-pygobject-dev  \
python3-typing python3-unixadmin python3-xml python3-xmlrpc  \
rgb shadow-dev shared-mime-info-dev slang sysfsutils sysklogd  \
sysstat systemd-dev update-rc.d usbutils util-linux-dev util-macros-dev  \
valgrind valgrind-dev wayland wayland-dbg wayland-dev wireless-tools  \
xcb-proto-dev xf86-input-libinput xkeyboard-config xkeyboard-config-dev  \
xorgproto-dev xrandr-dev xserver-xf86-config xtrans-dev zstd \
"

RCONFLICTS:${PN} += " ${YOCTO-DEPENDS-LIST} "
RREPLACES:${PN} += " ${YOCTO-DEPENDS-LIST} "
RPROVIDES:${PN} += " ${YOCTO-DEPENDS-LIST} "

RPROVIDES:${PN}:ubuntu += " libglib-2.0 "

do_install[network] = "1"

QA_EMPTY_DIRS = ""

python do_package:append() {
    os.environ['PSEUDO_DISABLED'] = '1'
    d.setVar('ERROR_QA', '')
}
