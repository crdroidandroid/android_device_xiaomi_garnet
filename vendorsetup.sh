echo 'Time to cook crDroid v12.x'

echo 'Cloning device tree'
git clone https://github.com/crdroidandroid/android_device_xiaomi_garnet -b 16.0 device/xiaomi/garnet

echo 'Cloning vendor tree'
git clone https://github.com/crdroidandroid/proprietary_vendor_xiaomi_garnet -b 16.0 vendor/xiaomi/garnet

echo 'Cloning kernel'
git clone https://github.com/crdroidandroid/android_xiaomi_kernel_garnet -b 16.0 kernel/xiaomi/garnet

echo 'Cloning kernel modules'
git clone https://github.com/MySelly/android_kernel_xiaomi_garnet-modules -b lineage-23.0 kernel/xiaomi/garnet-modules

echo 'Cloning kernel devicetrees'
git clone https://github.com/MySelly/android_kernel_xiaomi_garnet-devicetrees -b lineage-23.0 kernel/xiaomi/garnet-devicetrees

echo 'Cloning hardware'
git clone https://github.com/MySelly/android_hardware_xiaomi_garnet hardware/xiaomi

echo 'Cloning display'
rm -rf hardware/qcom-caf/sm8450/display && git clone https://github.com/MySelly/hardware_qcom-caf_sm8450_display hardware/qcom-caf/sm8450/display

echo 'Cloning camera'
git clone https://github.com/stuff-garnet/vendor_xiaomi_miuicamera-garnet vendor/xiaomi/miuicamera-garnet && git clone https://github.com/stuff-garnet/device_xiaomi_miuicamera-garnet device/xiaomi/miuicamera-garnet
