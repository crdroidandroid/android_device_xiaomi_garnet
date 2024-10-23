/*
     Copyright (C) 2024 The LineageOS Project
     SPDX-License-Identifier: Apache-2.0
 */

#include <vector>

#include <android-base/properties.h>
#define _REALLY_INCLUDE_SYS__SYSTEM_PROPERTIES_H_
#include <sys/_system_properties.h>

using android::base::GetProperty;

std::vector<std::string> ro_props_default_source_order = {
    "",
    "bootimage.",
    "odm.",
    "odm_dlkm.",
    "product.",
    "system.",
    "system_ext.",
    "vendor.",
    "vendor_dlkm.",
};

void property_override(char const prop[], char const value[], bool add = true)
{
    prop_info *pi;

    pi = (prop_info *) __system_property_find(prop);
    if (pi)
        __system_property_update(pi, value, strlen(value));
    else if (add)
        __system_property_add(prop, strlen(prop), value, strlen(value));
}

void set_ro_build_prop(const std::string &prop, const std::string &value) {
    for (const auto &source : ro_props_default_source_order) {
        auto prop_name = "ro." + source + "build." + prop;
        if (source == "")
            property_override(prop_name.c_str(), value.c_str());
        else
            property_override(prop_name.c_str(), value.c_str(), false);
    }
};

void set_ro_product_prop(const std::string &prop, const std::string &value) {
    for (const auto &source : ro_props_default_source_order) {
        auto prop_name = "ro.product." + source + prop;
        property_override(prop_name.c_str(), value.c_str(), false);
    }
};

void vendor_load_properties() {
    std::string region;
    std::string sku;
    std::string hwversion;
    region = GetProperty("ro.boot.hwc", "");
    sku = GetProperty("ro.boot.hardware.sku", "");
    hwversion = GetProperty("ro.boot.hwversion", "");

    std::string model;
    std::string brand;
    std::string device;
    std::string fingerprint;
    std::string description;
    std::string marketname;
    std::string mod_device;

    if (region == "IN") {
        device = "garnet";
        mod_device = "garnet_in_global";

        if (sku == "garnet") {
            brand = "Redmi";
            description = "garnet_in-user 14 UKQ1.231003.002 V816.0.8.0.UNRINXM release-keys";
            fingerprint = "Redmi/garnet_in/garnet:14/UKQ1.231003.002/V816.0.8.0.UNRINXM/release-keys";
            marketname = "Redmi Note 13 Pro 5G";
            model = "2312DRA50I";
        } else {
            brand = "POCO";
            description = "garnetp_in-user 14 UKQ1.231003.002 V816.0.8.0.UNRINXM release-keys";
            fingerprint = "POCO/garnetp_in/garnet:14/UKQ1.231003.002/V816.0.8.0.UNRINXM/release-keys";
            marketname = "POCO X6 5G";
            model = "23122PCD1I";
        }
    } else if (region == "GL") {
        device = "garnet";
        mod_device = "garnet_global";

        if (sku == "garnet") {
            brand = "Redmi";
            description = "garnet_global-user 14 UKQ1.231003.002 V816.0.14.0.UNRMIXM release-keys";
            fingerprint = "Redmi/garnet_global/garnet:14/UKQ1.231003.002/V816.0.14.0.UNRMIXM:user/release-keys";
            marketname = "Redmi Note 13 Pro 5G";
            model = "2312DRA50G";
        } else {
            brand = "POCO";
            description = "garnetp_global-user 14 UKQ1.231003.002 V816.0.14.0.UNRMIXM release-keys";
            fingerprint = "POCO/garnetp_global/garnet:14/UKQ1.231003.002/V816.0.14.0.UNRMIXM:user/release-keys";
            marketname = "POCO X6 5G";
            model = "23122PCD1G";
        }
    } else if (region == "CN") {
        device = "garnet";
        mod_device = "garnet";
        brand = "Redmi";
        description = "garnet-user 14 UKQ1.231003.002 V816.0.7.0.UNRCNXM release-keys";
        fingerprint = "Redmi/garnet/garnet:14/UKQ1.231003.002/V816.0.7.0.UNRCNXM:user/release-keys";
        marketname = "Redmi Note 13 Pro";
        if (hwversion == "17.0.9" || hwversion == "17.1.9" || hwversion == "17.2.6" || hwversion == "17.2.9" || hwversion == "17.9.9") {
            model = "2312CRAD3C";
        } else {
            model = "2312DRA50C";
        }
    } else if (region == "JP") {
        device = "XIG05";
        mod_device = "garnet_jp_kd_global";
        brand = "Redmi";
        description = "XIG05_jp_kdi-user 14 UKQ1.231003.002 V816.0.2.0.UNRJPKD release-keys";
        fingerprint = "Redmi/XIG05_jp_kdi/XIG05:14/UKQ1.231003.002/V816.0.2.0.UNRJPKD:user/release-keys";
        marketname = "XIG05";
        model = "XIG05";
    }

    set_ro_build_prop("fingerprint", fingerprint);
    set_ro_product_prop("brand", brand);
    set_ro_product_prop("device", device);
    set_ro_product_prop("model", model);

    property_override("bluetooth.device.default_name", marketname.c_str());
    property_override("ro.boot.product.hardware.sku", region.c_str());
    property_override("ro.build.description", description.c_str());
    property_override("ro.product.marketname", marketname.c_str());
    property_override("vendor.usb.product_string", marketname.c_str());
    if (mod_device != "") {
        property_override("ro.product.mod_device", mod_device.c_str());
    }
}
