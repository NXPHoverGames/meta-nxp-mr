
# Boot target is multi-user.target; do not auto-start the Wayland compositor.
# The base weston-init recipe enables weston.service and weston.socket via the
# default SYSTEMD_AUTO_ENABLE="enable", which causes a boot-time
# "Failed to listen on weston.socket" failure. Ship both units but leave them
# disabled; weston can still be started manually (systemctl start weston).
SYSTEMD_AUTO_ENABLE:${PN} = "disable"

# In Ubuntu overlay images, the RPM preinst groupadd/useradd create/delete
# /etc/.pwd.lock whose inode gets reused by the next RPM-installed file,
# triggering a pseudo inode-reuse path-mismatch abort during do_rootfs.
# Override pkg_preinst with an early exit so the useradd-class-appended
# code is never reached during rootfs construction.
# User/group creation is deferred to first-boot via pkg_postinst_ontarget.
pkg_preinst:${PN}() {
#!/bin/sh
# weston user and wayland/render/seat groups created at first boot (see postinst_ontarget)
exit 0
}

pkg_postinst_ontarget:${PN}() {
    groupadd -r wayland  2>/dev/null || true
    groupadd -r render   2>/dev/null || true
    groupadd -r seat     2>/dev/null || true
    useradd --home ${WESTON_USER_HOME} --shell /bin/sh --user-group \
        -G video,input,render,seat,wayland ${WESTON_USER} 2>/dev/null || true
}

# Append to the do_install task to tweak weston.ini after it's installed into ${D}
do_install:append() {
    WESTON_INI="${D}${sysconfdir}/xdg/weston/weston.ini"

    if [ -f "${WESTON_INI}" ]; then
        # Flip use-g2d to false if currently set to true
        sed -i 's/\buse-g2d=true\b/use-g2d=false/' "${WESTON_INI}"
    fi
}
