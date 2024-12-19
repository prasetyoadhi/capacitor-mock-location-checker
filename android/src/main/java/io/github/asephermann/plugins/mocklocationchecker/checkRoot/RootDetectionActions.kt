package io.github.asephermann.plugins.mocklocationchecker.checkRoot

import android.os.Build

class RootDetectionActions private constructor() {
    companion object {
        // const val ACTION_IS_ROOTED = "isRooted"
        // const val ACTION_IS_ROOTED_WITH_BUSY_BOX = "isRootedWithBusyBox"
        // const val ACTION_IS_ROOTED_WITH_EMULATOR = "isRootedWithEmulator"
        // const val ACTION_IS_ROOTED_WITH_BUSY_BOX_WITH_EMULATOR = "isRootedWithBusyBoxWithEmulator"
        const val ACTION_DETECT_ROOT_MANAGEMENT_APPS = "detectRootManagementApps"
        const val ACTION_DETECT_POTENTIALLY_DANGEROUS_APPS = "detectPotentiallyDangerousApps"
        const val ACTION_DETECT_TEST_KEYS = "detectTestKeys"
        const val ACTION_CHECK_FOR_BUSY_BOX_BINARY = "checkForBusyBoxBinary"
        const val ACTION_CHECK_FOR_SU_BINARY = "checkForSuBinary"
        const val ACTION_CHECK_SU_EXISTS = "checkSuExists"
        const val ACTION_CHECK_FOR_RW_PATHS = "checkForRWPaths"
        const val ACTION_CHECK_FOR_DANGEROUS_PROPS = "checkForDangerousProps"
        const val ACTION_CHECK_FOR_ROOT_NATIVE = "checkForRootNative"
        const val ACTION_DETECT_ROOT_CLOAKING_APPS = "detectRootCloakingApps"
        const val ACTION_IS_SELINUX_FLAG_ENABLED = "isSelinuxFlagInEnabled"
        const val ACTION_IS_EXIST_BUILD_TAGS = "isExistBuildTags"
        const val ACTION_DOES_SUPERUSER_APK_EXIST = "doesSuperuserApkExist"
        const val ACTION_IS_EXIST_SU_PATH = "isExistSUPath"
        const val ACTION_CHECK_DIR_PERMISSIONS = "checkDirPermissions"
        const val ACTION_CHECK_EXECUTING_COMMANDS = "checkExecutingCommands"
        const val ACTION_CHECK_INSTALLED_PACKAGES = "checkInstalledPackages"
        const val ACTION_CHECK_FOR_OVER_THE_AIR_CERTIFICATES = "checkforOverTheAirCertificates"
        const val ACTION_IS_RUNNING_ON_EMULATOR = "isRunningOnEmulator"
        const val ACTION_SIMPLE_CHECK_EMULATOR = "simpleCheckEmulator"
        const val ACTION_SIMPLE_CHECK_SDK_BF86 = "simpleCheckSDKBF86"
        const val ACTION_SIMPLE_CHECK_QR_REF_PH = "simpleCheckQRREFPH"
        const val ACTION_SIMPLE_CHECK_BUILD = "simpleCheckBuild"
        const val ACTION_CHECK_GENYMOTION = "checkGenymotion"
        const val ACTION_CHECK_GENERIC = "checkGeneric"
        const val ACTION_CHECK_GOOGLE_SDK = "checkGoogleSDK"
        // const val ACTION_TO_GET_DEVICE_INFO = "togetDeviceInfo"
    }

    // ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---
    init {
        throw InstantiationException("This class is not for instantiation")
    }
}