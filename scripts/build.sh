#!/bin/bash

if [[ $(lsb_release -c | grep "noble") ]]; then
    echo "apparmor_restrict_unprivileged_userns disabled to run bitbake, only lasts this login session."
    echo 0 | sudo tee /proc/sys/kernel/apparmor_restrict_unprivileged_userns
fi

BASE_WORK_DIR="$HOME/nxp-mr-image"
BUILD_TYPE=jazzy
BRANCH=lf-6.6.23-2.0.0-scarthgap
MACHINE="imx8mpnavq"
MANIFEST="imx-6.6.23-2.0.0.xml"
DISTRO="imx-desktop-xwayland"
SETUP="imx-setup-release.sh"
BUILDDIR="build-desktop"
NXP_META_IMAGE="meta-nxp-mr"
BUILD=`date +%Y%m%d.%H%M`; start=`date +%s`

mkdir -p ~/bin
curl https://storage.googleapis.com/git-repo-downloads/repo  > ~/bin/repo
chmod a+x ~/bin/repo
PATH=${PATH}:~/bin

mkdir -p ${BASE_WORK_DIR}
cd ${BASE_WORK_DIR}
repo init \
    -u https://github.com/nxp-imx/imx-manifest.git \
    -b imx-linux-scarthgap \
    -m ${MANIFEST}

repo sync -j`nproc`

DISTRO=${DISTRO} MACHINE=${MACHINE} EULA=yes source ${SETUP} -b ${BUILDDIR} || exit $?

get_yocto_hash() {
    local githash=$(git rev-parse --short=10 HEAD)
    echo "$githash"
}

cd ${BASE_WORK_DIR}/sources
for i in ${NXP_META_IMAGE}; do
    if [ -d ${i} ]; then
        pushd ${i}
        git pull
        popd
    else
        git clone -b $BRANCH git@github.com:NXPHoverGames/${i}.git || exit $?
    fi
    if [ $i = "${NXP_META_IMAGE}" ]; then
        pushd $i
            yocto_hash=$(get_yocto_hash)
        popd
    fi
done

cd ${BASE_WORK_DIR}/build-desktop/conf
if ! grep -q "/sources/${NXP_META_IMAGE}" bblayers.conf
then
cat >> bblayers.conf <<EOF
BBLAYERS += "\${BSPDIR}/sources/${NXP_META_IMAGE}"
EOF
fi
if ! grep -q "MACHINE ??= 'imx8mpnavqdesktop'" local.conf
then
sed -i "s/^MACHINE ??=.*/MACHINE ??= 'imx8mpnavqdesktop'/" local.conf
fi
if ! grep -q "DISTRO ?= 'imx-desktop-xwayland'" local.conf
then
sed -i "s/^DISTRO ?=.*/DISTRO ?= 'imx-desktop-xwayland'/" local.conf
fi
if ! grep -q 'PACKAGE_CLASSES = "package_rpm"' local.conf
then
sed -i 's/^PACKAGE_CLASSES =.*/PACKAGE_CLASSES = "package_rpm"/' local.conf
fi
if ! grep -q 'DISTRO_FEATURES:remove = " wayland alsa"' local.conf
then
cat >> local.conf <<EOF
DISTRO_FEATURES:remove = " wayland alsa"
EOF
fi
if ! grep -q 'PARALLEL_MAKE =' local.conf
then
cat >> local.conf <<EOF
PARALLEL_MAKE = "-j $(echo "$(nproc) / 4" | bc)"
EOF
fi
if ! grep -q 'BB_NUMBER_THREADS =' local.conf
then
cat >> local.conf <<EOF
BB_NUMBER_THREADS = "$(echo "$(nproc) / 2" | bc)"
EOF
fi

RELEASE_VER="${BUILD_TYPE}-$(date +%y%m%d%H%M%S)-${yocto_hash}"

echo $RELEASE_VER > ${BASE_WORK_DIR}/sources/${NXP_META_IMAGE}/recipes-fsl/images/files/release || exit $?

cd ${BASE_WORK_DIR}
source setup-environment build-desktop
bitbake imx-image-ros -c cleansstate
bitbake imx-image-ros

finish=`date +%s`; echo "### Build Time = `expr \( $finish - $start \) / 60` minutes"
