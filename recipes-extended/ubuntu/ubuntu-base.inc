#
# Generically build a ubuntu-base package set.
#

inherit nativeaptinstall

APTGET_CHROOT_DIR = "${D}"

S = "${WORKDIR}/rootfs"
B = "${S}"

ROOTFS="ubuntu-base-${UBUNTU_TARGET_VERSION}-base-${UBUNTU_TARGET_ARCH}.tar.gz"
SRC_URI = " \
	http://cdimage.ubuntu.com/ubuntu-base/releases/${UBUNTU_TARGET_BASEVERSION}/release/${ROOTFS};unpack=0;subdir=rootfs \
	file://fstab \
"

# Note that we still populate_sysroot because otherwise manifest
# updates through staging would not happen.
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_populate_sdk[noexec] = "1"

# Add user with password user and default shell bash
USER_SHELL_BASH ?= "/bin/bash"
# Password: user
USER_PASSWD_USER ?= "zHipsIr46vtZk"
APTGET_ADD_USERS ?= "user:${USER_PASSWD_USER}:${USER_SHELL_BASH}"

# As this package will never be built/compiled, we do not really need
# to look at any build- or runtime-dependencies or debug symbols!
# We have quite a number of deficiencies to ignore. The Desktop packages
# and rootfs do not really conform to Yocto rules.
INSANE_SKIP:${PN} += "already-stripped build-deps file-rdeps dev-so staticdev"
INHIBIT_DEFAULT_DEPS = "1"

# We need to keep all the other recipes happy that will rely on us
# during runtime through default dependencies!
RCONFLICTS:${PN} += "glibc eglibc"
RREPLACES:${PN} += "glibc eglibc"
RPROVIDES:${PN} += "glibc eglibc"

# The basic ubuntu-base rootfs does not provide enough functionality to
# enable ethernet or interfaces automatically in general.
# We support ifconfig, route, busybox, udhcpc
# which in conjunction should be enough to start up the Ethernet and
# do a local apt-get install of major packages.
APTGET_EXTRA_PACKAGES += "\
	udev \
	sudo htop iproute2 \
	whiptail \
	kmod gnupg net-tools netbase \
	netplan.io \
    network-manager \
    iputils-ping \
    nano \
    vim \
    bash-completion \
    locales \
    curl \
    wget \
"
# We *MUST* provide all Desktop packages in ubuntu-base that we need
# to install Yocto packages! Packages installed in images via
# IMAGE_INSTALL will not help to resolve the dependency chain!
# The following is an incomplete hint at dependency needs to accomodate
# Yocto packages:
# - dtc, xz-utils: LS2 restool references libs
# - python*, bc, db*: kernel-devsrc has scripts
# Note how we translate Desktop names into Yocto names for proper
# handling.
APTGET_EXTRA_PACKAGES += "\
    bison \
    device-tree-compiler \
    libnss-db \
    libssl-dev \
    openssl \
    bc db5.3 \
    xz-utils bzip2 \
"
RPROVIDES:${PN}:ubuntu += " \
rtld(GNU_HASH) \
libc6 \
libc6-utils \
libc6-pcprofile \
libcrypto1.1 \
libgcc1-dev \
libssl1.1 \
update-alternatives \
"

# Ensure that all files of the tgz end up in our packages
FILES:${PN} += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/lib*${SOLIBS}"
FILES:${PN} += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/ld*${SOLIBS}"
FILES:${PN}-dev = ""
#FILES:${PN}-dev += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/lib*${SOLIBSDEV}"
#FILES:${PN}-dev += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/ld*${SOLIBSDEV}"
#FILES:${PN}-dev += "${libdir}/sudo/lib*${SOLIBSDEV}"
FILES:${PN}-dbg += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/.debug/lib*${SOLIBS}"
FILES:${PN}-dbg += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/.debug/lib*${SOLIBSDEV}"

FILES:${PN} += "/usr/include"
FILES:${PN} += "/usr/share /usr/lib /usr/local /usr/games /usr/src"
FILES:${PN} += "/dev"
FILES:${PN} += "/lib"
FILES:${PN} += "/run /dev /lib /media /boot /tmp /proc /opt /mnt /home /srv /sys /run /root"
FILES:${PN} += "/lib.usr-is-merged /sbin.usr-is-merged /bin.usr-is-merged"
FILES:${PN}-dbg:append = " ${localstatedir}/lib/dpkg/info/.debug"
FILES:${PN}-dbg:${TRANSLATED_TARGET_ARCH} += "${base_libdir}/${TRANSLATED_TARGET_ARCH}-linux-gnu/.debug"

fakeroot do_shell_prepare() {
	cd "${D}"
	rm -rf "*"
	tar -C "${D}" -xzf "${S}/${ROOTFS}"
}

fakeroot do_aptget_user_update:append() {

	set -x

	# Without an /etc/fstab, the rootfs remains read only
	install -m 0644 ${WORKDIR}/fstab ${D}${sysconfdir}/fstab

	# After we are done installing our extra packages, we
	# optionally kill the log directory content, so that we
	# don't clutter the target
	#rm -rf ${APTGET_CHROOT_DIR}/var/log/*

	# The info dir file causes a Yocto complaint.
	# Per Yocto docs it should never be packaged
	rm -f "${APTGET_CHROOT_DIR}${infodir}/dir"

	# There is an unfulfilled libldap dependency.
	# Fixing the dependency seems a bit tricky to do,
	# so we eliminate the single tool that causes
	# the dependency problem. UNDERSTAND AND FIX!
	#rm -f "${APTGET_CHROOT_DIR}/usr/lib/gnupg/gpgkeys_ldap"

	# The default ubuntu-base rootfs does not do filesystem
	# fixes on boot. Given the nature of the BlueBox, we want
	# to enable that by default
	if [ -f "${APTGET_CHROOT_DIR}/lib/init/vars.sh" ]; then
		sed -i "s/^#*FSCKFIX\s*=.*/FSCKFIX=yes/g" "${APTGET_CHROOT_DIR}/lib/init/vars.sh"
	fi

	set +x
}

python do_install() {
    bb.build.exec_func("do_shell_prepare", d)
    bb.build.exec_func("do_aptget_update", d)
}

PKGDEST = "${WORKDIR}/packages-split"
python prep_xattr_postinst() {
    # Tricky hack. We go through the dirs and files of the split
    # package to determine if there is any non-root uid/gid to preserve.
    # if so, we generate a postinst script that should do just that.
    pkgdest = d.getVar('PKGDEST', True)
    packages = d.getVar('PACKAGES', True).split()
    for pkg in packages:
        lines = []
        pkgpath = os.path.join(pkgdest, pkg)
        for basedir, dirnames, filenames in os.walk(pkgpath):
            basedirchroot = basedir[ len(pkgpath) : ]
            for f in dirnames + filenames:
                ff = os.path.join(basedir, f)
                s = os.lstat(ff)
                if s.st_uid != 0 or s.st_gid != 0:
                    lines.append(str(s.st_uid) + ":" + str(s.st_gid) + ' "' + os.path.join(basedirchroot, f) + '"')

        if lines:
            prefix = '#!/bin/sh -e\n' \
                 'if [ x"$D" = "x" ]; then\n' \ 
                 '    # Do nothing on target!\n' \
                 '    exit 1\n' \
                 'else\n'
            body = ''
            for l in lines:
                body += 'chroot "$D" chown ' + l + '\n'
            suffix = 'fi\n'

            d.setVar('pkg_postinst:' + pkg, prefix + body + suffix)
}
PACKAGEFUNCS =+ "prep_xattr_postinst"

COMPATIBLE_MACHINE = "ubuntu"
