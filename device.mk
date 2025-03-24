#
# Copyright (C) 2024 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

# Enable updating of APEXes
$(call inherit-product, $(SRC_TARGET_DIR)/product/updatable_apex.mk)

# Enable virtual A/B OTA
$(call inherit-product, $(SRC_TARGET_DIR)/product/virtual_ab_ota/launch_with_vendor_ramdisk.mk)

# Enable project quotas and casefolding for emulated storage without sdcardfs
$(call inherit-product, $(SRC_TARGET_DIR)/product/emulated_storage.mk)

# Setup dalvik vm configs
$(call inherit-product, frameworks/native/build/phone-xhdpi-6144-dalvik-heap.mk)

# Qualcomm
$(call inherit-product, hardware/qcom-caf/common/common.mk)

# Call the MiuiCamera setup
$(call inherit-product-if-exists, device/xiaomi/miuicamera-garnet/device.mk)

# A/B
AB_OTA_POSTINSTALL_CONFIG += \
    RUN_POSTINSTALL_system=true \
    POSTINSTALL_PATH_system=system/bin/otapreopt_script \
    FILESYSTEM_TYPE_system=ext4 \
    POSTINSTALL_OPTIONAL_system=true

AB_OTA_POSTINSTALL_CONFIG += \
    RUN_POSTINSTALL_vendor=true \
    POSTINSTALL_PATH_vendor=bin/checkpoint_gc \
    FILESYSTEM_TYPE_vendor=ext4 \
    POSTINSTALL_OPTIONAL_vendor=true

PRODUCT_PACKAGES += \
    checkpoint_gc \
    otapreopt_script

# API
BOARD_SHIPPING_API_LEVEL := 31
PRODUCT_SHIPPING_API_LEVEL := $(BOARD_SHIPPING_API_LEVEL)

# Audio
$(call soong_config_set,android_hardware_audio,run_64bit,true)

PRODUCT_PACKAGES += \
    android.hardware.audio@7.0-impl:64 \
    android.hardware.audio.effect@7.0-impl:64 \
    android.hardware.audio.service

PRODUCT_PACKAGES += \
    android.hardware.soundtrigger@2.3-impl:64

PRODUCT_PACKAGES += \
    vendor.qti.hardware.AGMIPC@1.0-service:64 \
    vendor.qti.hardware.pal@1.0-impl:64

PRODUCT_PACKAGES += \
    audioadsprpcd:64 \
    audio.r_submix.default:64 \
    audio.usb.default:64 \
    sound_trigger.primary.parrot:64

PRODUCT_PACKAGES += \
    agmcap:64 \
    agmcompressplay:64 \
    agmplay:64 \
    libagm_compress_plugin:64 \
    libagm_mixer_plugin:64 \
    libagm_pcm_plugin:64 \
    libagmclient:64 \
    libagmmixer:64 \
    libbatterylistener:64 \
    libfmpal:64 \
    libpalclient:64 \
    libqcompostprocbundle:64 \
    libqcomvisualizer:64 \
    libqcomvoiceprocessing:64 \
    libsndcardparser:64 \
    libvolumelistener:64

AUDIO_HAL_DIR := hardware/qcom-caf/sm8450/audio/primary-hal

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.audio.low_latency.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.audio.low_latency.xml \
    frameworks/native/data/etc/android.hardware.audio.pro.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.audio.pro.xml \
    frameworks/native/data/etc/android.software.midi.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.midi.xml

PRODUCT_COPY_FILES += \
    $(AUDIO_HAL_DIR)/configs/parrot/microphone_characteristics.xml:$(TARGET_COPY_OUT_VENDOR)/etc/microphone_characteristics.xml \
    $(LOCAL_PATH)/configs/audio/backend_conf.xml:$(TARGET_COPY_OUT_VENDOR)/etc/backend_conf.xml \
    $(LOCAL_PATH)/configs/audio/backend_conf_fs.xml:$(TARGET_COPY_OUT_VENDOR)/etc/backend_conf_fs.xml \
    $(LOCAL_PATH)/configs/audio/card-defs.xml:$(TARGET_COPY_OUT_VENDOR)/etc/card-defs.xml \
    $(LOCAL_PATH)/configs/audio/kvh2xml.xml:$(TARGET_COPY_OUT_VENDOR)/etc/kvh2xml.xml \
    $(LOCAL_PATH)/configs/audio/usecaseKvManager.xml:$(TARGET_COPY_OUT_VENDOR)/etc/usecaseKvManager.xml

PRODUCT_COPY_FILES += \
    $(AUDIO_HAL_DIR)/configs/parrot/audio_effects.conf:$(TARGET_COPY_OUT_VENDOR)/etc/audio/sku_parrot/audio_effects.conf \
    $(LOCAL_PATH)/configs/audio/audio_effects.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio/sku_parrot/audio_effects.xml \
    $(LOCAL_PATH)/configs/audio/audio_policy_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio/sku_parrot/audio_policy_configuration.xml \
    $(LOCAL_PATH)/configs/audio/audio_policy_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio/sku_parrot_qssi/audio_policy_configuration.xml \
    $(LOCAL_PATH)/configs/audio/mixer_paths_overlay_dynamic.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio/sku_parrot/foursemi/mixer_paths_overlay_dynamic.xml \
    $(LOCAL_PATH)/configs/audio/mixer_paths_overlay_dynamic.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio/sku_parrot/mixer_paths_overlay_dynamic.xml \
    $(LOCAL_PATH)/configs/audio/mixer_paths_overlay_static.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio/sku_parrot/mixer_paths_overlay_static.xml \
    $(LOCAL_PATH)/configs/audio/mixer_paths_overlay_static_foursemi.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio/sku_parrot/foursemi/mixer_paths_overlay_static.xml \
    $(LOCAL_PATH)/configs/audio/mixer_paths_parrot_qrd.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio/sku_parrot/foursemi/mixer_paths_parrot_qrd.xml \
    $(LOCAL_PATH)/configs/audio/mixer_paths_parrot_qrd.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio/sku_parrot/mixer_paths_parrot_qrd.xml \
    $(LOCAL_PATH)/configs/audio/resourcemanager_parrot_qrd.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio/sku_parrot/resourcemanager_parrot_qrd.xml \
    $(LOCAL_PATH)/configs/audio/resourcemanager_parrot_qrd_foursemi.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio/sku_parrot/foursemi/resourcemanager_parrot_qrd.xml \

PRODUCT_COPY_FILES += \
    frameworks/av/services/audiopolicy/config/audio_policy_volumes.xml:$(TARGET_COPY_OUT_VENDOR)/etc/audio_policy_volumes.xml \
    frameworks/av/services/audiopolicy/config/bluetooth_audio_policy_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/bluetooth_audio_policy_configuration.xml \
    frameworks/av/services/audiopolicy/config/default_volume_tables.xml:$(TARGET_COPY_OUT_VENDOR)/etc/default_volume_tables.xml \
    frameworks/av/services/audiopolicy/config/r_submix_audio_policy_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/r_submix_audio_policy_configuration.xml \
    frameworks/av/services/audiopolicy/config/usb_audio_policy_configuration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/usb_audio_policy_configuration.xml

# Bluetooth
PRODUCT_PACKAGES += \
    audio.bluetooth.default:64 \
    android.hardware.bluetooth@1.0.vendor:64 \
    android.hardware.bluetooth.audio-impl:64 \
    android.hardware.bluetooth.audio@2.1.vendor:64

PRODUCT_PACKAGES += \
    vendor.qti.hardware.bluetooth_audio@2.1.vendor:64 \
    vendor.qti.hardware.btconfigstore@1.0.vendor:64 \
    vendor.qti.hardware.btconfigstore@2.0.vendor:64

PRODUCT_PACKAGES += \
    com.dsi.ant@1.0.vendor:64

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.bluetooth.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.bluetooth.xml \
    frameworks/native/data/etc/android.hardware.bluetooth_le.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.bluetooth_le.xml

# Boot control
PRODUCT_PACKAGES += \
    android.hardware.boot-service.qti \
    android.hardware.boot-service.qti.recovery

# Camera
PRODUCT_PACKAGES += \
    android.hardware.camera.provider@2.7.vendor:64 \
    vendor.qti.hardware.camera.postproc@1.0.vendor:64

PRODUCT_PACKAGES += \
    libcamera2ndk_vendor:64 \
    libcamera_metadata.vendor:64 \
    libexif.vendor:64 \
    libutilscallstack.vendor:64 \
    liblz4.vendor:64 \
    libyuv.vendor:64

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.camera.flash-autofocus.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.camera.flash-autofocus.xml \
    frameworks/native/data/etc/android.hardware.camera.front.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.camera.front.xml \
    frameworks/native/data/etc/android.hardware.camera.full.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.camera.full.xml \
    frameworks/native/data/etc/android.hardware.camera.raw.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.camera.raw.xml

# Configstore
PRODUCT_PACKAGES += \
    vendor.qti.hardware.capabilityconfigstore@1.0.vendor:64

# Display
PRODUCT_PACKAGES += \
    android.hardware.graphics.mapper@4.0-impl-qti-display \
    vendor.qti.hardware.display.allocator-service \
    vendor.qti.hardware.display.composer-service

PRODUCT_PACKAGES += \
    init.qti.display_boot.rc:64 \
    init.qti.display_boot.sh:64

PRODUCT_PACKAGES += \
    libdisplayconfig.system.qti:64 \
    vendor.display.config@1.11.vendor:64 \
    vendor.qti.hardware.display.config-V5-ndk:64 \
    vendor.qti.hardware.display.config-V2-ndk_platform.vendor:64 \
    vendor.qti.hardware.display.mapper@2.0.vendor:64

PRODUCT_COPY_FILES += \
    hardware/qcom-caf/sm8450/display/config/snapdragon_color_libs_config.xml:$(TARGET_COPY_OUT_VENDOR)/etc/snapdragon_color_libs_config.xml

# DRM
PRODUCT_PACKAGES += \
    android.hardware.drm@1.4.vendor:64 \
    android.hardware.drm-service.clearkey:64 \
    libcrypto_shim.vendor:64

# Device-specific settings
PRODUCT_PACKAGES += \
    XiaomiParts

# Dynamic partitions
PRODUCT_USE_DYNAMIC_PARTITIONS := true

# Euicc
PRODUCT_PACKAGES += \
    XiaomiEuicc

# Fastbootd
PRODUCT_PACKAGES += \
    fastbootd

# Fingerprint
TARGET_HAS_UDFPS := true

PRODUCT_PACKAGES += \
    android.hardware.biometrics.fingerprint-service.xiaomi \
    libudfpshandler:64 \
    vendor.xiaomi.hardware.fx.tunnel@1.0.vendor:64

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.fingerprint.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.fingerprint.xml

# Gatekeeper
PRODUCT_PACKAGES += \
    android.hardware.gatekeeper@1.0.vendor:64

# Graphics
PRODUCT_PACKAGES += \
    libgui_vendor:64

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.opengles.aep.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.opengles.aep.xml \
    frameworks/native/data/etc/android.software.opengles.deqp.level-2021-03-01.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.opengles.deqp.level.xml

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.vulkan.compute-0.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.vulkan.compute-0.xml \
    frameworks/native/data/etc/android.hardware.vulkan.level-1.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.vulkan.level-1.xml \
    frameworks/native/data/etc/android.hardware.vulkan.version-1_1.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.vulkan.version-1_1.xml \
    frameworks/native/data/etc/android.software.vulkan.deqp.level-2021-03-01.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.vulkan.deqp.level.xml

# GPS
PRODUCT_PACKAGES += \
    android.hardware.gnss-V1-ndk_platform.vendor:64 \
    android.hardware.gnss.measurement_corrections@1.1.vendor:64 \
    android.hardware.gnss.visibility_control@1.0.vendor:64 \
    android.hardware.gnss@2.1.vendor:64

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.location.gps.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.location.gps.xml

# Health
PRODUCT_PACKAGES += \
    android.hardware.health-service.xiaomi \
    android.hardware.health-service.xiaomi_recovery

# HIDL
PRODUCT_PACKAGES += \
    android.hidl.memory.block@1.0.vendor:64 \
    libhidltransport.vendor:64 \
    libhwbinder.vendor:64

# Hotword
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/permissions/privapp-permissions-hotword.xml:$(TARGET_COPY_OUT_PRODUCT)/etc/permissions/privapp-permissions-hotword.xml

# IFAA
PRODUCT_PACKAGES += \
    IFAAService

# Init
PRODUCT_PACKAGES += \
    charger_fw_fstab.qti:64 \
    fstab.qcom:64 \
    init.garnet.rc:64 \
    init.qcom.rc:64 \
    init.recovery.qcom.rc:64 \
    init.target.rc:64 \
    ueventd-odm.rc:64 \
    ueventd.qcom.rc:64

PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/rootdir/etc/fstab.qcom:$(TARGET_COPY_OUT_VENDOR_RAMDISK)/first_stage_ramdisk/fstab.qcom

# IPACM
PRODUCT_PACKAGES += \
    ipacm \
    IPACM_cfg.xml \
    IPACM_Filter_cfg.xml

# IR
PRODUCT_PACKAGES += \
    android.hardware.ir-service.example

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.consumerir.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.consumerir.xml

# Keylayout
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/keylayout/parrot-qrd-snd-card_Button_Jack.kl:$(TARGET_COPY_OUT_VENDOR_RAMDISK)/usr/keylayout/parrot-qrd-snd-card_Button_Jack.kl

# Keymaster
PRODUCT_PACKAGES += \
    android.hardware.authsecret@1.0.vendor:64 \
    android.hardware.keymaster@4.1.vendor:64

PRODUCT_PACKAGES += \
    libkeymaster_messages.vendor:64

# Keymint
PRODUCT_PACKAGES += \
    android.hardware.hardware_keystore.xml:64 \
    android.hardware.security.keymint-V1-ndk_platform.vendor:64 \
    android.hardware.security.rkp-V3-ndk.vendor:64 \
    android.hardware.security.secureclock-V1-ndk_platform.vendor:64 \
    android.hardware.security.sharedsecret-V1-ndk_platform.vendor:64

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.keystore.app_attest_key.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.keystore.app_attest_key.xml

# Lights
PRODUCT_PACKAGES += \
    android.hardware.light-V1-ndk_platform.vendor

# Lineage Health
PRODUCT_PACKAGES += \
    vendor.lineage.health-service.default

# Media
PRODUCT_PACKAGES += \
    libcodec2_hidl@1.0.vendor

PRODUCT_PACKAGES += \
    libavservices_minijail \
    libavservices_minijail_vendor

PRODUCT_COPY_FILES += \
    $(AUDIO_HAL_DIR)/configs/common/codec2/service/1.0/c2audio.vendor.base-arm.policy:$(TARGET_COPY_OUT_VENDOR)/etc/seccomp_policy/c2audio.vendor.base-arm.policy \
    $(AUDIO_HAL_DIR)/configs/common/codec2/service/1.0/c2audio.vendor.base-arm64.policy:$(TARGET_COPY_OUT_VENDOR)/etc/seccomp_policy/c2audio.vendor.base-arm64.policy \
    $(AUDIO_HAL_DIR)/configs/common/codec2/service/1.0/c2audio.vendor.ext-arm.policy:$(TARGET_COPY_OUT_VENDOR)/etc/seccomp_policy/c2audio.vendor.ext-arm.policy \
    $(AUDIO_HAL_DIR)/configs/common/codec2/service/1.0/c2audio.vendor.ext-arm64.policy:$(TARGET_COPY_OUT_VENDOR)/etc/seccomp_policy/c2audio.vendor.ext-arm64.policy \

PRODUCT_COPY_FILES += \
    $(AUDIO_HAL_DIR)/configs/common/codec2/media_codecs_c2_audio.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_c2_audio.xml \
    $(AUDIO_HAL_DIR)/configs/common/media_codecs_vendor_audio.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_vendor_audio.xml

PRODUCT_COPY_FILES += \
    frameworks/av/media/libstagefright/data/media_codecs_google_video.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_google_video.xml \
    frameworks/av/media/libstagefright/data/media_codecs_google_video_le.xml:$(TARGET_COPY_OUT_VENDOR)/etc/media_codecs_google_video_le.xml

# Memtrack
PRODUCT_PACKAGES += \
    vendor.qti.hardware.memtrack-service

# Mlipay
PRODUCT_PACKAGES += \
    vendor.xiaomi.hardware.mfidoca@1.0.vendor \
    vendor.xiaomi.hardware.mlipay@1.1.vendor \
    vendor.xiaomi.hardware.mtdservice@1.3.vendor

# Network
PRODUCT_PACKAGES += \
    android.hardware.tetheroffload.config@1.0.vendor:64 \
    android.hardware.tetheroffload.control@1.1.vendor:64 \
    android.system.net.netd@1.1.vendor:64

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.software.ipsec_tunnel_migration.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.ipsec_tunnel_migration.xml \
    frameworks/native/data/etc/android.software.ipsec_tunnels.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.ipsec_tunnels.xml

# NFC
PRODUCT_PACKAGES += \
    android.hardware.nfc@1.2.vendor:64 \
    android.hardware.secure_element@1.2.vendor:64 \
    libchrome.vendor \
    nqnfcinfo

$(foreach sku, CN GL JP, \
    $(eval PRODUCT_COPY_FILES += \
        frameworks/native/data/etc/android.hardware.nfc.ese.xml:$(TARGET_COPY_OUT_ODM)/etc/permissions/sku_$(sku)/android.hardware.nfc.ese.xml \
        frameworks/native/data/etc/android.hardware.nfc.hce.xml:$(TARGET_COPY_OUT_ODM)/etc/permissions/sku_$(sku)/android.hardware.nfc.hce.xml \
        frameworks/native/data/etc/android.hardware.nfc.hcef.xml:$(TARGET_COPY_OUT_ODM)/etc/permissions/sku_$(sku)/android.hardware.nfc.hcef.xml \
        frameworks/native/data/etc/android.hardware.nfc.uicc.xml:$(TARGET_COPY_OUT_ODM)/etc/permissions/sku_$(sku)/android.hardware.nfc.uicc.xml \
        frameworks/native/data/etc/android.hardware.nfc.xml:$(TARGET_COPY_OUT_ODM)/etc/permissions/sku_$(sku)/android.hardware.nfc.xml \
        frameworks/native/data/etc/android.hardware.se.omapi.ese.xml:$(TARGET_COPY_OUT_VENDOR)etc/permissions/sku_$(sku)/android.hardware.se.omapi.ese.xml \
        frameworks/native/data/etc/android.hardware.se.omapi.uicc.xml:$(TARGET_COPY_OUT_VENDOR)etc/permissions/sku_$(sku)/android.hardware.se.omapi.uicc.xml \
        frameworks/native/data/etc/com.android.nfc_extras.xml:$(TARGET_COPY_OUT_ODM)/etc/permissions/sku_$(sku)/com.android.nfc_extras.xml \
        frameworks/native/data/etc/com.nxp.mifare.xml:$(TARGET_COPY_OUT_ODM)/etc/permissions/sku_$(sku)/com.nxp.mifare.xml))

# Overlay
PRODUCT_PACKAGES += \
    ApertureOverlayGarnet \
    CarrierConfigOverlayGarnet \
    DialerOverlayGarnet \
    FrameworkOverlayGarnet \
    FrameworkOverlayGarnetGLEsim \
    FrameworkOverlayGarnetJPEsim \
    LineageSDKOverlayGarnet \
    LineageSettingsOverlayGarnet \
    LineageSystemUIOverlayGarnet \
    SettingsOverlayGarnet \
    SettingsProviderOverlayGarnetPoco \
    SettingsProviderOverlayGarnetRedmi \
    SettingsProviderOverlayGarnetRedmiCN \
    SettingsProviderOverlayGarnetXIG05 \
    SystemUIOverlayGarnet \
    TelephonyOverlayGarnet \
    WifiOverlayGarnet \
    WifiOverlayGarnetPoco \
    WifiOverlayGarnetRedmi \
    WifiOverlayGarnetRedmiCN \
    WifiOverlayGarnetXIG05

# Parts
PRODUCT_PACKAGES += \
    XiaomiParts

# Power
PRODUCT_PACKAGES += \
    android.hardware.power-service-qti

PRODUCT_PACKAGES += \
    vendor.qti.hardware.perf@2.3.vendor

# Protobuf
PRODUCT_PACKAGES += \
    libprotobuf-cpp-full-3.9.1-vendorcompat:64 \
    libprotobuf-cpp-lite-3.9.1-vendorcompat:64

# QMI
PRODUCT_PACKAGES += \
    libcurl.vendor:64 \
    libjson:64 \
    libjsoncpp.vendor:64 \
    libqti_vndfwk_detect.vendor:64 \
    libsqlite.vendor:64 \
    libvndfwk_detect_jni.qti_vendor:64 \
    vendor.qti.hardware.systemhelper@1.0.vendor:64

# Radio
PRODUCT_PACKAGES += \
    android.hardware.radio.config@1.3.vendor:64 \
    android.hardware.radio.deprecated@1.0.vendor:64 \
    android.hardware.radio@1.6.vendor:64

PRODUCT_PACKAGES += \
    librmnetctl:64

# Sensors
PRODUCT_PACKAGES += \
    android.hardware.sensors-service.xiaomi-multihal:64 \
    android.frameworks.sensorservice@1.0.vendor:64 \
    libsensorndkbridge:64 \
    sensors.xiaomi.v2:64

PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/sensors/hals.conf:$(TARGET_COPY_OUT_VENDOR)/etc/sensors/hals.conf

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.sensor.compass.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.compass.xml \
    frameworks/native/data/etc/android.hardware.sensor.gyroscope.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.gyroscope.xml \
    frameworks/native/data/etc/android.hardware.sensor.light.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.light.xml \
    frameworks/native/data/etc/android.hardware.sensor.proximity.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.proximity.xml \
    frameworks/native/data/etc/android.hardware.sensor.stepcounter.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.stepcounter.xml \
    frameworks/native/data/etc/android.hardware.sensor.stepdetector.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.stepdetector.xml \
    frameworks/native/data/etc/android.hardware.sensor.stepdetector.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.sensor.stepdetector.xml

# Servicetracker
PRODUCT_PACKAGES += \
    vendor.qti.hardware.servicetracker@1.2.vendor:64

# Soong namespaces
PRODUCT_SOONG_NAMESPACES += \
    $(LOCAL_PATH) \
    hardware/xiaomi

# Telephony
PRODUCT_PACKAGES += \
    extphonelib \
    extphonelib-product \
    extphonelib.xml \
    extphonelib_product.xml \
    ims-ext-common \
    ims_ext_common.xml \
    qti-telephony-hidl-wrapper \
    qti-telephony-hidl-wrapper-prd \
    qti_telephony_hidl_wrapper.xml \
    qti_telephony_hidl_wrapper_prd.xml \
    qti-telephony-utils \
    qti-telephony-utils-prd \
    qti_telephony_utils.xml \
    qti_telephony_utils_prd.xml \
    QtiTelephonyCompat \
    telephony-ext \
    xiaomi-telephony-stub

PRODUCT_BOOT_JARS += \
    telephony-ext \
    xiaomi-telephony-stub

PRODUCT_PACKAGES += \
    qcrilNrDb_vendor

$(foreach sku, GL JP, \
    $(eval PRODUCT_COPY_FILES += \
        frameworks/native/data/etc/android.hardware.telephony.euicc.xml:$(TARGET_COPY_OUT_ODM)/etc/permissions/sku_$(sku)/android.hardware.telephony.euicc.xml))

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.telephony.cdma.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.telephony.cdma.xml \
    frameworks/native/data/etc/android.hardware.telephony.gsm.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.telephony.gsm.xml \
    frameworks/native/data/etc/android.hardware.telephony.ims.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.telephony.ims.xml \
    frameworks/native/data/etc/android.software.sip.voip.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.sip.voip.xml

# Thermal
PRODUCT_PACKAGES += \
    android.hardware.thermal-service.qti \
    android.hardware.thermal@2.0.vendor

# Touchscreen
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.touchscreen.multitouch.jazzhand.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.touchscreen.multitouch.jazzhand.xml

# Update engine
PRODUCT_PACKAGES += \
    update_engine \
    update_engine_sideload \
    update_verifier

# USB
PRODUCT_PACKAGES += \
    android.hardware.usb-service.qti:64 \
    android.hardware.usb.gadget-service.qti:64

PRODUCT_PACKAGES += \
    init.qcom.usb.rc \
    init.qcom.usb.sh \
    usb_compositions.conf

PRODUCT_SOONG_NAMESPACES += \
    vendor/qcom/opensource/usb/etc

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.usb.accessory.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.usb.accessory.xml \
    frameworks/native/data/etc/android.hardware.usb.host.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.usb.host.xml

# Vendor service manager
PRODUCT_PACKAGES += \
    vndservice \
    vndservicemanager

# Verified boot
PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.software.verified_boot.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.software.verified_boot.xml

# Vibrator
TARGET_QTI_VIBRATOR_EFFECT_LIB := libqtivibratoreffect.xiaomi
TARGET_QTI_VIBRATOR_USE_EFFECT_STREAM := true

PRODUCT_PACKAGES += \
    vendor.qti.hardware.vibrator.service.xiaomi

PRODUCT_COPY_FILES += \
    hardware/xiaomi/vibrator/excluded-input-devices.xml:$(TARGET_COPY_OUT_VENDOR)/etc/excluded-input-devices.xml

# Shims
PRODUCT_PACKAGES += \
    libhidlbase_shim \
    libinput_shim \
    libgui_shim

# WiFi
PRODUCT_PACKAGES += \
    android.hardware.wifi-service \
    android.hardware.wifi.hostapd@1.0.vendor \
    wpa_cli \
    wpa_supplicant \
    wpa_supplicant.conf \
    hostapd \
    hostapd_cli \
    libwifi-hal-ctrl:64 \
    libwifi-hal-qcom:64 \
    libkeystore-engine-wifi-hidl \
    libkeystore-wifi-hidl \
    libwifi-hal

PRODUCT_PACKAGES += \
    firmware_wlan_mac.bin_symlink \
    firmware_WCNSS_qcom_cfg.ini_symlink

PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/configs/wifi/WCNSS_qcom_cfg.ini:$(TARGET_COPY_OUT_VENDOR)/etc/wifi/adrastea/WCNSS_qcom_cfg.ini \
    $(LOCAL_PATH)/configs/wifi/p2p_supplicant_overlay.conf:$(TARGET_COPY_OUT_VENDOR)/etc/wifi/p2p_supplicant_overlay.conf \
    $(LOCAL_PATH)/configs/wifi/wpa_supplicant_overlay.conf:$(TARGET_COPY_OUT_VENDOR)/etc/wifi/wpa_supplicant_overlay.conf

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.wifi.direct.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.wifi.direct.xml \
    frameworks/native/data/etc/android.hardware.wifi.passpoint.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.wifi.passpoint.xml \
    frameworks/native/data/etc/android.hardware.wifi.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.wifi.xml

# WiFi Display
PRODUCT_PACKAGES += \
    android.media.audio.common.types-V2-cpp \
    libnl:64 \
    libwfdaac_vendor

# Vendor
$(call inherit-product, vendor/xiaomi/garnet/garnet-vendor.mk)
