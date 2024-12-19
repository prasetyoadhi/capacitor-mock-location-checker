package io.github.asephermann.plugins.mocklocationchecker.checkRoot

import android.content.Context
import android.util.Log
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.scottyab.rootbeer.RootBeer

@CapacitorPlugin(name = "RootJailBreakDetector")
class RootJailBreakDetectorPlugin : Plugin() {
    private val implementation = RootJailBreakDetector()

    /**
     * Generic method to check rooted status with various configurations.
     */
    private fun checkRootStatus(
        context: Context,
        useBusyBox: Boolean,
        checkEmulator: Boolean
    ): Boolean {
        val rootBeer = RootBeer(context)
        val rootBeerCheck = if (useBusyBox) rootBeer.isRootedWithBusyBoxCheck else rootBeer.isRooted
        val internalCheck = if (checkEmulator) {
            implementation.checkIsRootedWithEmulator(activity)
        } else {
            implementation.checkIsRooted(activity)
        }

        Log.d(Constants.LOG_TAG, "[checkRootStatus] RootBeerCheck: $rootBeerCheck, InternalCheck: $internalCheck")
        return rootBeerCheck || internalCheck
    }

    /**
     * Check root status using RootBeer and internal checks.
     */
    @PluginMethod
    fun isRooted(call: PluginCall) {
        val ret = JSObject()
        try {
            val context = activity.applicationContext
            val isRooted = checkRootStatus(context, useBusyBox = false, checkEmulator = false)
            ret.put("isRooted", isRooted)
            call.resolve(ret)
        } catch (error: Exception) {
            handlePluginError(call, "isRooted", error)
        }
    }

    /**
     * Check root status using RootBeer with BusyBox and internal checks.
     */
    @PluginMethod
    fun isRootedWithBusyBox(call: PluginCall) {
        val ret = JSObject()
        try {
            val context = activity.applicationContext
            val isRooted = checkRootStatus(context, useBusyBox = true, checkEmulator = false)
            ret.put("isRooted", isRooted)
            call.resolve(ret)
        } catch (error: Exception) {
            handlePluginError(call, "isRootedWithBusyBox", error)
        }
    }

    /**
     * Check root status using RootBeer, internal checks, and emulator detection.
     */
    @PluginMethod
    fun isRootedWithEmulator(call: PluginCall) {
        val ret = JSObject()
        try {
            val context = activity.applicationContext
            val isRooted = checkRootStatus(context, useBusyBox = false, checkEmulator = true)
            ret.put("isRooted", isRooted)
            call.resolve(ret)
        } catch (error: Exception) {
            handlePluginError(call, "isRootedWithEmulator", error)
        }
    }

    /**
     * Check root status using RootBeer with BusyBox, internal checks, and emulator detection.
     */
    @PluginMethod
    fun isRootedWithBusyBoxWithEmulator(call: PluginCall) {
        val ret = JSObject()
        try {
            val context = activity.applicationContext
            val isRooted = checkRootStatus(context, useBusyBox = true, checkEmulator = true)
            ret.put("isRooted", isRooted)
            call.resolve(ret)
        } catch (error: Exception) {
            handlePluginError(call, "isRootedWithBusyBoxWithEmulator", error)
        }
    }

    /**
     * Perform a specific root detection action.
     */
    @PluginMethod
    fun whatIsRooted(call: PluginCall) {
        val ret = JSObject()
        try {
            val action = call.getString("action") ?: throw IllegalArgumentException("Action parameter is missing")
            val context = activity.applicationContext
            val rootBeer = RootBeer(context)

            val isRooted = when (action) {
                RootDetectionActions.ACTION_DETECT_ROOT_MANAGEMENT_APPS -> rootBeer.detectRootManagementApps()
                RootDetectionActions.ACTION_DETECT_POTENTIALLY_DANGEROUS_APPS -> rootBeer.detectPotentiallyDangerousApps()
                RootDetectionActions.ACTION_DETECT_TEST_KEYS -> rootBeer.detectTestKeys()
                RootDetectionActions.ACTION_CHECK_FOR_BUSY_BOX_BINARY -> rootBeer.checkForBusyBoxBinary()
                RootDetectionActions.ACTION_CHECK_FOR_SU_BINARY -> rootBeer.checkForSuBinary()
                RootDetectionActions.ACTION_CHECK_SU_EXISTS -> rootBeer.checkSuExists()
                RootDetectionActions.ACTION_CHECK_FOR_RW_PATHS -> rootBeer.checkForRWPaths()
                RootDetectionActions.ACTION_CHECK_FOR_DANGEROUS_PROPS -> rootBeer.checkForDangerousProps()
                RootDetectionActions.ACTION_CHECK_FOR_ROOT_NATIVE -> rootBeer.checkForRootNative()
                RootDetectionActions.ACTION_DETECT_ROOT_CLOAKING_APPS -> rootBeer.detectRootCloakingApps()
                RootDetectionActions.ACTION_IS_SELINUX_FLAG_ENABLED -> Utils.isSelinuxFlagInEnabled
                else -> implementation.whatIsRooted(action, context)
            }

            Log.d(Constants.LOG_TAG, "[whatIsRooted] Action: $action, Result: $isRooted")
            ret.put("isRooted", isRooted)
            call.resolve(ret)
        } catch (error: Exception) {
            handlePluginError(call, "whatIsRooted", error)
        }
    }

    /**
     * Retrieve detailed device information.
     */
    @PluginMethod
    fun getDeviceInfo(call: PluginCall) {
        try {
            val ret = implementation.getDeviceInfo()
            Log.d(Constants.LOG_TAG, "[getDeviceInfo] Result: $ret")
            call.resolve(ret)
        } catch (error: Exception) {
            handlePluginError(call, "getDeviceInfo", error)
        }
    }

    /**
     * Utility method for handling errors in plugin methods.
     */
    private fun handlePluginError(call: PluginCall, methodName: String, error: Exception) {
        Log.e(Constants.LOG_TAG, "[$methodName] Error: ${error.localizedMessage}", error)
        call.reject(error.message, error)
    }
}