# A more complex image with ROS elements
require imx-image-mr.bb

CUSTOM_FILES_PATH := "${THISDIR}/files"
SRC_URI = " \
    file://install_cognipilot.sh \
    file://release \
"

IMAGE_INSTALL += "\
    install-interface-config \
"

ROS_VERSION = "jazzy"

ROS_PPA = "http://packages.ros.org/ros2/ubuntu;https://raw.githubusercontent.com/ros/rosdistro/master/ros.key;ros-archive-keyring.gpg;deb;ros-latest.list"

ROS_PACKAGES = "  \
    ros-dev-tools \
    ros-${ROS_VERSION}-ros-base \
    ros-${ROS_VERSION}-gscam \
    ros-${ROS_VERSION}-camera-calibration \
    ros-${ROS_VERSION}-camera-calibration-parsers \
    ros-${ROS_VERSION}-camera-info-manager \
    ros-${ROS_VERSION}-compressed-image-transport \
    ros-${ROS_VERSION}-cv-bridge \
    ros-${ROS_VERSION}-foxglove-bridge \
    ros-${ROS_VERSION}-gscam \
    ros-${ROS_VERSION}-image-pipeline \
    ros-${ROS_VERSION}-image-tools \
    ros-${ROS_VERSION}-image-transport \
    ros-${ROS_VERSION}-image-transport-plugins \
    ros-${ROS_VERSION}-launch-testing-ament-cmake \
    ros-${ROS_VERSION}-nav2-bringup \
    ros-${ROS_VERSION}-topic-tools \
    ros-${ROS_VERSION}-vision-opencv \
    ros-${ROS_VERSION}-ackermann-msgs \
    ros-${ROS_VERSION}-action-msgs \
    ros-${ROS_VERSION}-actionlib-msgs \
    ros-${ROS_VERSION}-actuator-msgs \
    ros-${ROS_VERSION}-apriltag-msgs \
    ros-${ROS_VERSION}-aruco-msgs \
    ros-${ROS_VERSION}-aruco-opencv-msgs \
    ros-${ROS_VERSION}-can-msgs \
    ros-${ROS_VERSION}-cartographer-ros-msgs \
    ros-${ROS_VERSION}-control-msgs \
    ros-${ROS_VERSION}-controller-manager-msgs \
    ros-${ROS_VERSION}-dataspeed-can-msg-filters \
    ros-${ROS_VERSION}-diagnostic-msgs \
    ros-${ROS_VERSION}-dwb-msgs \
    ros-${ROS_VERSION}-event-camera-msgs \
    ros-${ROS_VERSION}-foxglove-msgs \
    ros-${ROS_VERSION}-gazebo-msgs \
    ros-${ROS_VERSION}-geographic-msgs \
    ros-${ROS_VERSION}-geometry-msgs \
    ros-${ROS_VERSION}-gps-msgs \
    ros-${ROS_VERSION}-graph-msgs \
    ros-${ROS_VERSION}-grid-map-msgs \
    ros-${ROS_VERSION}-irobot-create-msgs \
    ros-${ROS_VERSION}-lifecycle-msgs \
    ros-${ROS_VERSION}-map-msgs \
    ros-${ROS_VERSION}-nav-2d-msgs \
    ros-${ROS_VERSION}-nav-msgs \
    ros-${ROS_VERSION}-nav2-msgs \
    ros-${ROS_VERSION}-nmea-msgs \
    ros-${ROS_VERSION}-object-recognition-msgs \
    ros-${ROS_VERSION}-octomap-msgs \
    ros-${ROS_VERSION}-ouster-sensor-msgs \
    ros-${ROS_VERSION}-pcl-msgs \
    ros-${ROS_VERSION}-pendulum-msgs \
    ros-${ROS_VERSION}-plotjuggler-msgs \
    ros-${ROS_VERSION}-point-cloud-msg-wrapper \
    ros-${ROS_VERSION}-polygon-msgs \
    ros-${ROS_VERSION}-radar-msgs \
    ros-${ROS_VERSION}-rclpy-message-converter-msgs \
    ros-${ROS_VERSION}-robot-calibration-msgs \
    ros-${ROS_VERSION}-rosapi-msgs \
    ros-${ROS_VERSION}-rosbridge-msgs \
    ros-${ROS_VERSION}-rosbridge-test-msgs \
    ros-${ROS_VERSION}-rosgraph-msgs \
    ros-${ROS_VERSION}-rqt-msg \
    ros-${ROS_VERSION}-rtcm-msgs \
    ros-${ROS_VERSION}-rviz-2d-overlay-msgs \
    ros-${ROS_VERSION}-sensor-msgs \
    ros-${ROS_VERSION}-sensor-msgs-py \
    ros-${ROS_VERSION}-shape-msgs \
    ros-${ROS_VERSION}-statistics-msgs \
    ros-${ROS_VERSION}-std-msgs \
    ros-${ROS_VERSION}-stereo-msgs \
    ros-${ROS_VERSION}-system-modes-msgs \
    ros-${ROS_VERSION}-teleop-tools-msgs \
    ros-${ROS_VERSION}-test-msgs \
    ros-${ROS_VERSION}-tf2-geometry-msgs \
    ros-${ROS_VERSION}-tf2-msgs \
    ros-${ROS_VERSION}-tf2-sensor-msgs \
    ros-${ROS_VERSION}-trajectory-msgs \
    ros-${ROS_VERSION}-twist-mux-msgs \
    ros-${ROS_VERSION}-ublox-msgs \
    ros-${ROS_VERSION}-ublox-ubx-msgs \
    ros-${ROS_VERSION}-udp-msgs \
    ros-${ROS_VERSION}-unique-identifier-msgs \
    ros-${ROS_VERSION}-v4l2-camera \
    ros-${ROS_VERSION}-vision-msgs \
    ros-${ROS_VERSION}-vision-msgs-layers \
    ros-${ROS_VERSION}-vision-msgs-rviz-plugins \
    ros-${ROS_VERSION}-visualization-msgs \
    ros-${ROS_VERSION}-rmw \
    ros-${ROS_VERSION}-rmw-cyclonedds-cpp \
    ros-${ROS_VERSION}-rmw-dds-common \
    ros-${ROS_VERSION}-rmw-implementation \
    ros-${ROS_VERSION}-rmw-implementation-cmake \
    build-essential \
    cmake \
    git \
    htop \
    ccache \
    can-utils \
    pkg-config \
    python3-colcon-common-extensions \
    python3-flake8 \
    python3-pip \
    python3-dev \
    python3-pytest-cov \
    python3-rosdep \
    python3-setuptools \
    python3-testresources \
    python3-vcstool \
    python3-argcomplete \
    python3-ipython \
    python3-netifaces \
    python3-empy \
    python3-jinja2 \
    python3-cerberus \
    python3-coverage \
    python3-matplotlib \
    python3-numpy \
    python3-packaging \
    python3-pkgconfig \
    python3-opencv \
    python3-wheel \
    python3-requests \
    python3-serial \
    python3-six \
    python3-toml \
    python3-psutil \
    python3-pysolar \
    python3-can \
    python3-hid \
    g++ \
    gcc \
    gdb \
    ninja-build \
    make \
    bzip2 \
    zip \
    rsync \
    shellcheck \
    tzdata \
    unzip \
    valgrind \
    xsltproc \
    binutils \
    bc \
    libyaml-cpp-dev \
    autoconf \
    automake \
    bison \
    ca-certificates \
    openssh-client \
    cppcheck \
    dirmngr \
    doxygen \
    file \
    gosu \
    lcov \
    libfreetype6-dev \
    libgtest-dev \
    libpng-dev \
    libssl-dev \
    libopencv-dev \
    flex \
    genromfs \
    gperf \
    libncurses-dev \
    libtool \
    uncrustify \
    vim-common \
    libxml2-utils \
    mesa-utils \
    libeigen3-dev \
    protobuf-compiler \
    libimage-exiftool-perl \
    v4l-utils \
    gstreamer1.0-nice \
    gstreamer1.0-opencv \
    iw \
    usbutils \
    iperf \
    nethogs \
    screen \
"

PYTHON_ROSDEP_PACKAGE = "${@bb.utils.contains('ROS_VERSION', 'jazzy', 'python3-rosdep', 'python3-rosdep', d)}"

ADD_ROS_PACKAGES ?= "${ROS_PACKAGES}"
APTGET_EXTRA_PACKAGES_LAST += " \
    ${ADD_ROS_PACKAGES} \
"

APTGET_EXTRA_PPA += "${ROS_PPA}"

ROOTFS_POSTPROCESS_COMMAND += "do_install_home_files;"

fakeroot do_install_home_files() {
    install -m 0755 ${CUSTOM_FILES_PATH}/install_cognipilot.sh ${APTGET_CHROOT_DIR}/home/user
    install -m 0644 ${CUSTOM_FILES_PATH}/release ${IMAGE_ROOTFS}${sysconfdir}/
    chmod 755 ${APTGET_CHROOT_DIR}/home/user/install_cognipilot.sh
    install -m 0755 ${CUSTOM_FILES_PATH}/tflite_runtime-2.19.0-cp312-cp312-linux_aarch64.whl ${APTGET_CHROOT_DIR}/tmp
    chroot ${APTGET_CHROOT_DIR} /usr/bin/pip3 install ${APTGET_CHROOT_DIR}/tmp/tflite_runtime-2.19.0-cp312-cp312-linux_aarch64.whl
}
