# Route the Ara2 runtime service to the journal only. Upstream ships the unit
# with StandardOutput/StandardError set to "journal+console", which clutters the
# boot tty and echoes accelerator-probe failures when no Ara2 device is attached.
# Logs stay available via journalctl.

# Adapt 'ExecStart' field in such way that the service won't fail in case the
# script fails. This prevents errors at boot in case no Ara2 is mounted in the
# M.2 slot.

ARA2_SERVICE = "${D}${sysconfdir}/systemd/system/rt-sdk-ara2.service"

do_install:append() {
    sed -i 's/=journal+console$/=journal/' "${ARA2_SERVICE}"
    sed -i "/^ExecStart=/i # Return true if script fails to prevent errors at boot when no Ara2 is available" "${ARA2_SERVICE}"
    sed -i "s/^\(ExecStart=\/bin\/bash\) \(.*\)$/\1 -c '\2 || true'/" "${ARA2_SERVICE}"
}
