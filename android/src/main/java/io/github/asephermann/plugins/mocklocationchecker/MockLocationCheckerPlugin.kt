package io.github.asephermann.plugins.mocklocationchecker

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@CapacitorPlugin(name = "MockLocationChecker")
class MockLocationCheckerPlugin : Plugin() {
    private val implementation = MockLocationChecker()

    /**
     * Mengecek apakah ada aplikasi mock location selain whitelist.
     */
    @PluginMethod
    fun checkMock(call: PluginCall)  {
        val whiteList = call.getArray("whiteList")
        val ret = JSObject()

        val result = implementation.checkMock(activity, whiteList.toList())

        ret.put("isMock", result.isMock)
        ret.put("messages", result.messages)
        ret.put("indicated", result.indicated)
        call.resolve(ret)
    }

    /**
     * Mengecek apakah lokasi terakhir berasal dari mock provider (suspend).
     */
    @PluginMethod
    fun isLocationFromMockProvider(call: PluginCall)  {
        val ret = JSObject()
        CoroutineScope(Dispatchers.Main).launch {
            val result = implementation.isLocationFromMockProvider(activity)
            ret.put("value", result)
            call.resolve(ret)
        }
    }

    /**
     * Membuka detail aplikasi mock location di pengaturan.
     */
    @PluginMethod
    fun goToMockLocationAppDetail(call: PluginCall)  {
        val packageName = call.getString("packageName")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse(String.format("package:%s", packageName))
            activity.startActivity(intent)
        }
    }

    /**
     * Mendapatkan lokasi dan mengecek apakah lokasi tersebut mock (menggunakan coroutine).
     */
    @PluginMethod
    fun checkMockGeoLocation(call: PluginCall) {        
        CoroutineScope(Dispatchers.Main).launch {
            val result = implementation.checkMockGeoLocation(activity)
            val ret = JSObject()
            ret.put("isMock", result.isMock)
            ret.put("messages", result.messages)
            ret.put("indicated", result.indicated)
            call.resolve(ret)
        }
    }
}