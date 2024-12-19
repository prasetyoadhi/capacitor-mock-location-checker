package io.github.asephermann.plugins.mocklocationchecker.checkRoot

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import com.getcapacitor.JSObject
import org.json.JSONException
import java.io.File


class RootJailBreakDetector {

    /**
     * Internal checks.
     */
    fun checkIsRooted(activity: Activity): Boolean {
        val c1 = isExistBuildTags
        val c2 = doesSuperuserApkExist()
        val c3 = isExistSUPath
        val c4 = checkDirPermissions()
        val c5 = checkExecutingCommands()
        val c6 = checkInstalledPackages(activity.applicationContext)
        val c7 = checkforOverTheAirCertificates()

        Log.d(Constants.LOG_TAG, "check c1 = isExistBuildTags: $c1")
        Log.d(Constants.LOG_TAG, "check c2 = doesSuperuserApkExist: $c2")
        Log.d(Constants.LOG_TAG, "check c3 = isExistSUPath: $c3")
        Log.d(Constants.LOG_TAG, "check c4 = checkDirPermissions: $c4")
        Log.d(Constants.LOG_TAG, "check c5 = checkExecutingCommands: $c5")
        Log.d(Constants.LOG_TAG, "check c6 = checkInstalledPackages: $c6")
        Log.d(Constants.LOG_TAG, "check c7 = checkforOverTheAirCertificates: $c7")

        val result = c1 || c2 || c3 || c4 || c5 || c6 || c7
        Log.d(Constants.LOG_TAG, String.format("[checkDirPermissions] result: %s", result))
        return result
    }

    /**
     * Running on emulator check and Internal checks
     */
    fun checkIsRootedWithEmulator(context: Context): Boolean {
        val c1 = isExistBuildTags
        val c2 = doesSuperuserApkExist()
        val c3 = isExistSUPath
        val c4 = checkDirPermissions()
        val c5 = checkExecutingCommands()
        val c6 = checkInstalledPackages(context)
        val c7 = checkforOverTheAirCertificates()
        val c8 = isRunningOnEmulator

        Log.d(Constants.LOG_TAG, "check c1 = isExistBuildTags: $c1")
        Log.d(Constants.LOG_TAG, "check c2 = doesSuperuserApkExist: $c2")
        Log.d(Constants.LOG_TAG, "check c3 = isExistSUPath: $c3")
        Log.d(Constants.LOG_TAG, "check c4 = checkDirPermissions: $c4")
        Log.d(Constants.LOG_TAG, "check c5 = checkExecutingCommands: $c5")
        Log.d(Constants.LOG_TAG, "check c6 = checkInstalledPackages: $c6")
        Log.d(Constants.LOG_TAG, "check c7 = checkforOverTheAirCertificates: $c7")
        Log.d(Constants.LOG_TAG, "check c8 = isRunningOnEmulator: $c8")

        val result = c1 || c2 || c3 || c4 || c5 || c6 || c7 || c8
        Log.d(Constants.LOG_TAG, String.format("[checkDirPermissions] result: %s", result))
        return result
    }

    fun whatIsRooted(action: String?, context: Context): Boolean {
        var result = false
        when (action) {
            RootDetectionActions.ACTION_IS_EXIST_BUILD_TAGS -> result = isExistBuildTags
            RootDetectionActions.ACTION_DOES_SUPERUSER_APK_EXIST -> result = doesSuperuserApkExist()
            RootDetectionActions.ACTION_IS_EXIST_SU_PATH -> result = isExistSUPath
            RootDetectionActions.ACTION_CHECK_DIR_PERMISSIONS -> result = checkDirPermissions()
            RootDetectionActions.ACTION_CHECK_EXECUTING_COMMANDS -> result = checkExecutingCommands()
            RootDetectionActions.ACTION_CHECK_INSTALLED_PACKAGES -> result = checkInstalledPackages(context)
            RootDetectionActions.ACTION_CHECK_FOR_OVER_THE_AIR_CERTIFICATES -> result = checkforOverTheAirCertificates()
            RootDetectionActions.ACTION_IS_RUNNING_ON_EMULATOR -> result = isRunningOnEmulator
            RootDetectionActions.ACTION_SIMPLE_CHECK_EMULATOR,
            RootDetectionActions.ACTION_SIMPLE_CHECK_SDK_BF86,
            RootDetectionActions.ACTION_SIMPLE_CHECK_QR_REF_PH,
            RootDetectionActions.ACTION_SIMPLE_CHECK_BUILD,
            RootDetectionActions.ACTION_CHECK_GENYMOTION,
            RootDetectionActions.ACTION_CHECK_GENERIC,
            RootDetectionActions.ACTION_CHECK_GOOGLE_SDK -> result = whatIsRunningOnEmulator(action)
            else -> Log.e(Constants.LOG_TAG, "[WhatisRooted] Unknown action: $action")
        }
        return result
    }
    
    /**
     * Checks whether any of the system directories are writable or the /data directory is readable.
     * This test will usually result in a false negative on rooted devices.
     */
    private fun checkDirPermissions(): Boolean {
        var isWritableDir: Boolean
        var isReadableDataDir: Boolean
        var result = false
        for (dirName in Constants.PATHS_THAT_SHOULD_NOT_BE_WRITABLE) {
            val currentDir = File(dirName)
            isWritableDir = currentDir.exists() && currentDir.canWrite()
            isReadableDataDir = dirName == "/data" && currentDir.canRead()
            if (isWritableDir || isReadableDataDir) {
                Log.d(Constants.LOG_TAG, String.format("[checkDirPermissions] check [%s] => [isWritable:%s][isReadableData:%s]", dirName, isWritableDir, isReadableDataDir))
                result = true
            }
        }
        Log.d(Constants.LOG_TAG, String.format("[checkDirPermissions] result: %s", result))
        return result
    }

    /**
     * Checking the BUILD tag for test-keys. By default, stock Android ROMs from Google are built with release-keys tags.
     * If test-keys are present, this can mean that the Android build on the device is either a developer build
     * or an unofficial Google build.
     *
     * For example: Nexus 4 is running stock Android from Google’s (Android Open Source Project) AOSP.
     * This is why the build tags show "release-keys".
     *
     * > root@android:/ # cat /system/build.prop | grep ro.build.tags
     * > ro.build.tags=release-keys
     */
    private val isExistBuildTags: Boolean
        get() {
            var result = false
            try {
                val buildTags = Constants.ANDROID_OS_BUILD_TAGS

                // Log.d(Constants.LOG_TAG, String.format("[isExistBuildTags] buildTags: %s", buildTags));
                result = buildTags != null && buildTags.contains("test-keys")
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, String.format("[isExistBuildTags] Error: %s", e.message))
            }
            Log.d(Constants.LOG_TAG, String.format("[isExistBuildTags] result: %s", result))
            return result
        }

    /**
     * Checks whether the Superuser.apk is present in the system applications.
     *
     * Superuser.apk. This package is most often looked for on rooted devices.
     * Superuser allows the user to authorize applications to run as root on the device.
     */
    private fun doesSuperuserApkExist(): Boolean {
        var result = false
        for (path in Constants.SUPER_USER_APK_FILES) {
            val rootFile = File(path)
            if (rootFile.exists()) {
                Log.d(
                    Constants.LOG_TAG,
                    String.format("[doesSuperuserApkExist] found SU apk: %s", path)
                )
                result = true
            }
        }
        Log.d(Constants.LOG_TAG, String.format("[doesSuperuserApkExist] result: %s", result))
        return result
    }


    /**
     * Checking if SU path exist (case sensitive).
     */
    private val isExistSUPath: Boolean
        get() {
            val pathsArray = Constants.SU_PATHES.toTypedArray()
            var result = false
            for (path in pathsArray) {
                val completePath = path + "su"
                val suPath = File(completePath)
                val fileExists = suPath.exists()
                if (fileExists) {
                    Log.d(Constants.LOG_TAG, String.format("[isExistSUPath] binary [%s] detected!", path))
                    result = true
                }
            }
            Log.d(Constants.LOG_TAG, String.format("[isExistSUPath] result: %s", result))
            return result
        }

    /**
     * Checks for installed packages which are known to be present on rooted devices.
     *
     * @param context Used for accessing the package manager.
     */
    private fun checkInstalledPackages(context: Context): Boolean {
        val pm = context.packageManager
        val installedPackages = pm.getInstalledPackages(0)
        var rootOnlyAppCount = 0
        for (packageInfo in installedPackages) {
            val packageName = packageInfo.packageName
            if (Constants.BLACKLISTED_PACKAGES.contains(packageName)) {
                Log.d(Constants.LOG_TAG, String.format("[checkInstalledPackages] Package [%s] found in BLACKLISTED_PACKAGES", packageName))
                return true
            }
            if (Constants.ROOT_ONLY_APPLICATIONS.contains(packageName)) {
                Log.d(Constants.LOG_TAG, String.format("[checkInstalledPackages] Package [%s] found in ROOT_ONLY_APPLICATIONS", packageName))
                rootOnlyAppCount += 1
            }

            // Check to see if the Cydia Substrate exists.
            if (Constants.CYDIA_SUBSTRATE_PACKAGE == packageName) {
                Log.d(Constants.LOG_TAG, String.format("[checkInstalledPackages] Package [%s] found in CYDIA_SUBSTRATE_PACKAGE", packageName))
                rootOnlyAppCount += 1
            }
        }
        Log.d(Constants.LOG_TAG,String.format("[checkInstalledPackages] count of root-only apps: %s", rootOnlyAppCount))
        val result = rootOnlyAppCount > 2 // todo: why?
        Log.d(Constants.LOG_TAG, String.format("[checkInstalledPackages] result: %s", result))
        return result
    }

    /**
     * Checking for Over The Air (OTA) certificates.
     *
     * By default, Android is updated OTA using public certs from Google. If the certs are not there,
     * this usually means that there is a custom ROM installed which is updated through other means.
     *
     * For example: Nexus 4 has no custom ROM and is updated through Google. Updating this device however, will probably break root.
     * > 1|bullhead:/ $ ls -l /etc/security/otacerts.zip
     * > -rw-r--r-- 1 root root 1544 2009-01-01 09:00 /etc/security/otacerts.zip
     */
    private fun checkforOverTheAirCertificates(): Boolean {
        val otacerts = File(Constants.OTA_CERTIFICATES_PATH)
        val exist = otacerts.exists()
        val result = !exist
        Log.d(Constants.LOG_TAG, String.format("[checkforOverTheAirCertificates] exist: %s", exist))
        Log.d(Constants.LOG_TAG, String.format("[checkforOverTheAirCertificates] result: %s", result))
        return result
    }

     /**
     * Checking if possible to call SU command.
     *
     * @see <a href="https://github.com/xdhfir/xdd/blob/0df93556e4b8605057196ddb9a1c10fbc0f6e200/yeshttp/baselib/src/main/java/com/my/baselib/lib/utils/root/RootUtils.java">TODO: check xdhfir RootUtils.java</a>
     * @see <a href="https://github.com/xdhfir/xdd/blob/0df93556e4b8605057196ddb9a1c10fbc0f6e200/yeshttp/baselib/src/main/java/com/my/baselib/lib/utils/root/ExecShell.java">TODO: check xdhfir ExecShell.java</a>
     * @see <a href="https://github.com/huohong01/truck/blob/master/app/src/main/java/com/hsdi/NetMe/util/RootUtils.java">adopted huohong01 RootUtils.java</a>
     * @see <a href="https://github.com/tansiufang54/fncgss/blob/master/app/src/main/java/co/id/franknco/controller/RootUtil.java">adopted tansiufang54 RootUtils.java</a>
     */
    private fun checkExecutingCommands(): Boolean {
        val c1: Boolean = Utils.canExecuteCommand("/system/xbin/which su")
        val c2: Boolean = Utils.canExecuteCommand("/system/bin/which su")
        val c3: Boolean = Utils.canExecuteCommand("which su")
        val result = c1 || c2 || c3
        Log.d(Constants.LOG_TAG, String.format("[checkExecutingCommands] result [%s] => [c1:%s][c2:%s][c3:%s]", result, c1, c2, c3))
        return result
    }

    /**
     * Simple implementation.
     * <p>
     * TODO: move in another class.
     * TODO: check this repos:
     *
     * @see <a href="https://github.com/strazzere/anti-emulator">anti-emulator</a>
     * @see <a href="https://github.com/framgia/android-emulator-detector">android-emulator-detector</a>
     * @see <a href="https://github.com/testmandy/NativeAdLibrary-master/blob/68e1a972fc746a0b51395f813f5bcf32fd619376/library/src/main/java/me/dt/nativeadlibary/util/RootUtil.java#L59">testmandy RootUtil.java</a>
     */
    val isRunningOnEmulator: Boolean
        get() {
            Utils.deviceInfo
            val simpleCheck = (Build.MODEL.contains("Emulator") 
                        // ||Build.FINGERPRINT.startsWith("unknown") // Meizu Mx Pro will return unknown, so comment it!
                        || Build.MODEL.contains("Android SDK built for x86")
                        || Build.BOARD == "QC_Reference_Phone" 
                        || Build.HOST.startsWith("Build")) //MSI App Player
            val checkGenymotion = Build.MANUFACTURER.contains("Genymotion")
            val checkGeneric = Build.FINGERPRINT.startsWith("generic") || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            val checkGoogleSDK = Build.MODEL.contains("google_sdk") || "google_sdk" == Build.PRODUCT

            val checkSDKEmulator = ((Build.FINGERPRINT.startsWith("google/sdk_gphone")
                        && (Build.FINGERPRINT.endsWith(":user/release-keys") 
                        || Build.FINGERPRINT.endsWith(":userdebug/dev-keys"))
                        && Build.MANUFACTURER == "Google" && Build.PRODUCT.startsWith("sdk_gphone") && Build.BRAND == "google"
                        && Build.MODEL.startsWith("sdk_gphone"))
                        //
                        || Build.FINGERPRINT.startsWith("generic")
                        // || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")
                        //bluestacks
                        || "QC_Reference_Phone" == Build.BOARD && !"Xiaomi".equals(Build.MANUFACTURER,ignoreCase = true) //bluestacks
                        || Build.MANUFACTURER.contains("Genymotion")
                        || Build.HOST.startsWith("Build") //MSI App Player
                        || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                        || Build.PRODUCT == "google_sdk")
                        // another Android SDK emulator check
                        // || SystemProperties.getProp("ro.kernel.qemu") == "1"

            val result = simpleCheck || checkGenymotion || checkGeneric || checkGoogleSDK || checkSDKEmulator
            Log.d(
                Constants.LOG_TAG, String.format(
                    "[isRunningOnEmulator] result [%s] => [simpleCheck:%s][checkGenymotion:%s][checkGeneric:%s][checkGoogleSDK:%s][checkSDKEmulator:%s]",
                    result,
                    simpleCheck,
                    checkGenymotion,
                    checkGeneric,
                    checkGoogleSDK,
                    checkSDKEmulator
                )
            )
            return result
        }

    fun whatIsRunningOnEmulator(action: String?): Boolean {
        Utils.deviceInfo
        var result = false
        when (action) {
            RootDetectionActions.ACTION_SIMPLE_CHECK_EMULATOR -> result = Build.MODEL.contains("Emulator")
            RootDetectionActions.ACTION_SIMPLE_CHECK_SDK_BF86 -> result = Build.MODEL.contains("Android SDK built for x86")
            RootDetectionActions.ACTION_SIMPLE_CHECK_QR_REF_PH -> result = Build.BOARD == "QC_Reference_Phone"
            RootDetectionActions.ACTION_SIMPLE_CHECK_BUILD -> result = Build.HOST.startsWith("Build")
            RootDetectionActions.ACTION_CHECK_GENYMOTION -> result = Build.MANUFACTURER.contains("Genymotion")
            RootDetectionActions.ACTION_CHECK_GENERIC -> result = Build.FINGERPRINT.startsWith("generic") || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            RootDetectionActions.ACTION_CHECK_GOOGLE_SDK -> result = Build.MODEL.contains("google_sdk") || "google_sdk" == Build.PRODUCT
        }
        return result
    }


    @Throws(JSONException::class)
    fun getDeviceInfo(): JSObject {
        Utils.deviceInfo
        val objBuild = JSObject()
        objBuild.put("DEVICE", Build.DEVICE)
        objBuild.put("MODEL", Build.MODEL)
        objBuild.put("MANUFACTURER", Build.MANUFACTURER)
        objBuild.put("BRAND", Build.BRAND)
        objBuild.put("BOARD", Build.BOARD)
        objBuild.put("HARDWARE", Build.HARDWARE)
        objBuild.put("PRODUCT", Build.PRODUCT)
        objBuild.put("FINGERPRINT", Build.FINGERPRINT)
        objBuild.put("HOST", Build.HOST)
        // Add More info
        objBuild.put("USER", Build.USER)
        objBuild.put("OSNAME", System.getProperty("os.name"))
        objBuild.put("OSVERSION", System.getProperty("os.version"))
        objBuild.put("V_INCREMENTAL", Build.VERSION.INCREMENTAL)
        objBuild.put("V_RELEASE", Build.VERSION.RELEASE)
        objBuild.put("V_SDK_INT", Build.VERSION.SDK_INT)
        return objBuild
    }

    // TODO: https://github.com/tansiufang54/fncgss/blob/master/app/src/main/java/co/id/franknco/controller/RootUtil.java#L126
    //    private boolean checkServerSocket() {
    //        try {
    //            ServerSocket ss = new ServerSocket(81);
    //            ss.close();
    //            return true;
    //        } catch (Exception e) {
    //            // not sure
    //        }
    //        return false;
    //    }
}