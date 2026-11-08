/*
 * Copyright (C) 2022-2025 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#define LOG_TAG "UdfpsHandler.garnet"

#include <aidl/android/hardware/biometrics/fingerprint/BnFingerprint.h>
#include <android-base/logging.h>
#include <android-base/properties.h>
#include <android-base/unique_fd.h>

#include <fcntl.h>
#include <poll.h>
#include <unistd.h>
#include <fstream>
#include <thread>

#include "UdfpsHandler.h"

#define COMMAND_NIT 10
#define PARAM_NIT_FOD 1
#define PARAM_NIT_NONE 0

#define COMMAND_FOD_PRESS_STATUS 1
#define PARAM_FOD_PRESSED 1
#define PARAM_FOD_RELEASED 0

#define DISPLAY_PATH "/sys/devices/platform/soc/soc:qcom,dsi-display-primary"
#define LOCAL_HBM_PATH DISPLAY_PATH "/local_hbm"
#define FOD_UI_PATH DISPLAY_PATH "/fod_ui"
#define LOCAL_HBM_OFF 0
#define LOCAL_HBM_1000NIT 1000

using ::aidl::android::hardware::biometrics::fingerprint::AcquiredInfo;

namespace {

template <typename T>
static void set(const std::string& path, const T& value) {
    std::ofstream file(path);
    file << value;
}

static bool readBool(int fd) {
    char c;

    if (lseek(fd, 0, SEEK_SET) < 0 || read(fd, &c, sizeof(c)) != 1) {
        PLOG(ERROR) << "failed to read " << FOD_UI_PATH;
        return false;
    }

    return c != '0';
}

}  // anonymous namespace

class XiaomiGarnetUdfpsHandler : public UdfpsHandler {
  public:
    void init(fingerprint_device_t* device) {
        mDevice = device;
        if (!mDevice) {
            LOG(ERROR) << "no fingerprint device";
            return;
        }

        // Thread to listen for fod ui changes
        std::thread([this]() {
            android::base::unique_fd fd(open(FOD_UI_PATH, O_RDONLY));
            if (fd < 0) {
                PLOG(ERROR) << "failed to open " << FOD_UI_PATH;
                return;
            }

            struct pollfd fodUiPoll = {
                    .fd = fd.get(),
                    .events = POLLERR | POLLPRI,
                    .revents = 0,
            };

            readBool(fd.get());
            while (true) {
                int rc = poll(&fodUiPoll, 1, -1);
                if (rc < 0) {
                    PLOG(ERROR) << "failed to poll " << FOD_UI_PATH;
                    continue;
                }

                mDevice->extCmd(mDevice, COMMAND_NIT,
                                readBool(fd.get()) ? PARAM_NIT_FOD : PARAM_NIT_NONE);
            }
        }).detach();
    }

    void onFingerDown(uint32_t /*x*/, uint32_t /*y*/, float /*minor*/, float /*major*/) {
        if (mAuthSuccess || !mDevice) return;
        LOG(INFO) << __func__;

        mDevice->extCmd(mDevice, COMMAND_FOD_PRESS_STATUS, PARAM_FOD_PRESSED);

        // Request HBM
        set(LOCAL_HBM_PATH, LOCAL_HBM_1000NIT);
    }

    void onFingerUp() {
        if (!mDevice) return;
        LOG(DEBUG) << __func__;

        mDevice->extCmd(mDevice, COMMAND_FOD_PRESS_STATUS, PARAM_FOD_RELEASED);

        // Disable HBM
        set(LOCAL_HBM_PATH, LOCAL_HBM_OFF);
    }

    void onAcquired(int32_t result, int32_t vendorCode) {
        LOG(DEBUG) << __func__ << " result: " << result << " vendorCode: " << vendorCode;
        switch (static_cast<AcquiredInfo>(result)) {
            case AcquiredInfo::GOOD:
            case AcquiredInfo::PARTIAL:
            case AcquiredInfo::INSUFFICIENT:
            case AcquiredInfo::SENSOR_DIRTY:
            case AcquiredInfo::TOO_SLOW:
            case AcquiredInfo::TOO_FAST:
            case AcquiredInfo::TOO_DARK:
            case AcquiredInfo::TOO_BRIGHT:
            case AcquiredInfo::IMMOBILE:
            case AcquiredInfo::LIFT_TOO_SOON:
                onFingerUp();
                break;
            default:
                break;
        }
    }

    void onAuthenticationSucceeded() {
        mAuthSuccess = true;
        onFingerUp();
        std::thread([this]() {
            std::this_thread::sleep_for(std::chrono::milliseconds(500));
            mAuthSuccess = false;
        }).detach();
    }

    void onAuthenticationFailed() { onFingerUp(); }

  private:
    fingerprint_device_t* mDevice = nullptr;
    bool mAuthSuccess = false;
};

static UdfpsHandler* create() {
    return new XiaomiGarnetUdfpsHandler();
}

static void destroy(UdfpsHandler* handler) {
    delete handler;
}

extern "C" UdfpsHandlerFactory UDFPS_HANDLER_FACTORY = {
        .create = create,
        .destroy = destroy,
};
