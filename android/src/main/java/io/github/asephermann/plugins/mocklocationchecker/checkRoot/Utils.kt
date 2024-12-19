package io.github.asephermann.plugins.mocklocationchecker.checkRoot

import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader


class Utils private constructor() {
    companion object {
        /**
         * In Development - an idea of ours was to check the if selinux is enforcing - this could be disabled for some rooting apps
         * Checking for selinux mode
         *
         * @return true if selinux enabled
         */
        val isSelinuxFlagInEnabled: Boolean
            get() {
                try {
                    val c = Class.forName("android.os.SystemProperties")
                    val get = c.getMethod("get", String::class.java)
                    val selinux = get.invoke(c, "ro.build.selinux") as String
                    return "1" == selinux
                } catch (ignored: Exception) {
                }
                return false
            }

        /**
         * Helper function that logs the error and then calls the error callback.
         */
        fun getPluginResultError(from: String?, e: Throwable) {
            val message = String.format("[%s] Error: %s", from, e.message)
            Log.e(Constants.LOG_TAG, message, e)
        }

        /**
         * Executes a command on the system.
         *
         * @param command A Command.
         */
        fun canExecuteCommand(command: String?): Boolean {
            var process: Process? = null
            return try {
                process = Runtime.getRuntime().exec(command)
                val `in` = BufferedReader(InputStreamReader(process.inputStream))
                `in`.readLine() != null
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, String.format("[canExecuteCommand] Error: %s", e.message))
                false
            } finally {
                process?.destroy()
            }
        }

        /**
         * Get device information.
         */
        val deviceInfo: Unit
            get() {
                Log.d(Constants.LOG_TAG,String.format("[getDeviceInfo][%20s][%s]", "Build.DEVICE", Build.DEVICE))
                Log.d(Constants.LOG_TAG,String.format("[getDeviceInfo][%20s][%s]", "Build.MODEL", Build.MODEL))
                Log.d(Constants.LOG_TAG,String.format("[getDeviceInfo][%20s][%s]", "Build.MANUFACTURER", Build.MANUFACTURER))
                Log.d(Constants.LOG_TAG,String.format("[getDeviceInfo][%20s][%s]", "Build.BRAND", Build.BRAND))
                Log.d(Constants.LOG_TAG,String.format("[getDeviceInfo][%20s][%s]", "Build.HARDWARE", Build.HARDWARE))
                Log.d(Constants.LOG_TAG,String.format("[getDeviceInfo][%20s][%s]", "Build.PRODUCT", Build.PRODUCT))
                Log.d(Constants.LOG_TAG,String.format("[getDeviceInfo][%20s][%s]", "Build.FINGERPRINT", Build.FINGERPRINT))
                Log.d(Constants.LOG_TAG,String.format("[getDeviceInfo][%20s][%s]", "Build.HOST", Build.HOST))
            }
    }

    init {
        throw InstantiationException("This class is not for instantiation")
    }
}
